package com.vidyo.vidyoconnector.bl.connector.media.local.screen_share.source

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.core.content.getSystemService
import androidx.core.graphics.applyCanvas
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoFormat
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoFrame
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.logD
import com.vidyo.vidyoconnector.utils.logE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/**
 * Created by Rostyslav.Lesovyi
 */
class DeviceScreenShareSource(private val context: Context) {
    companion object : Loggable.Tag("DeviceScreenShareSource") {
        private val handler = Handler(Looper.getMainLooper())
    }

    private val windowManager = context.getSystemService<WindowManager>()
    private val projectionManager = context.getSystemService<MediaProjectionManager>()

    private val bitmapsCache = HashMap<String, Bitmap>()

    fun createScreenCaptureIntent(): Intent? {
        return projectionManager?.createScreenCaptureIntent()
    }

    fun handleScreenCaptureResult(result: ActivityResult): Flow<VirtualVideoFrame>? {
        logD { "handleScreenCaptureResult: result = $result" }

        val manager = projectionManager ?: return null
        val windowManager = windowManager ?: return null

        val data = result.data ?: return null
        val projection = manager.getMediaProjection(result.resultCode, data) ?: return null

        return flow { worker(this, projection, windowManager) }
    }

    private suspend fun worker(
        collector: FlowCollector<VirtualVideoFrame>,
        projection: MediaProjection,
        windowManager: WindowManager,
    ) {
        logD { "worker: started" }

        val mediaReadyChannel = Channel<Boolean>(Channel.CONFLATED)
        val mediaFinishedChannel = Channel<Boolean>(Channel.CONFLATED)
        val mediaCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                logD { "worker: media projection stopped" }
                mediaFinishedChannel.trySend(true)
            }
        }

        var active = true
        var mirrorInfo: MirrorInfo? = null
        try {
            projection.registerCallback(mediaCallback, handler)
            while (active) {
                val displaySize = getDisplaySize(windowManager)
                if (mirrorInfo?.displaySize != displaySize) {
                    mirrorInfo?.release()

                    logD { "worker: reconfigure, displaySize = $displaySize" }

                    mediaReadyChannel.trySend(false)
                    mirrorInfo = MirrorInfo(context, projection, displaySize) {
                        mediaReadyChannel.trySend(true)
                    }
                }

                active = select {
                    mediaReadyChannel.onReceive.invoke {
                        if (!it) {
                            return@invoke true
                        }
                        mirrorInfo.imageReader.acquireLatestImage()?.use {
                            val frame = withContext(Dispatchers.Default) {
                                convertImageToVideoFrame(it)
                            }
                            collector.emit(frame)
                        }
                        return@invoke true
                    }
                    mediaFinishedChannel.onReceive.invoke {
                        return@invoke false
                    }
                }
            }
        } catch (e: Exception) {
            when (e is CancellationException) {
                true -> logD { "worker: cancelled" }
                else -> logE(e) { "worker: failed" }
            }
            throw e
        } finally {
            logD { "worker: finished" }

            mirrorInfo?.release()

            bitmapsCache.clear()

            projection.unregisterCallback(mediaCallback)
            projection.stop()
        }
    }

    private fun convertImageToVideoFrame(image: Image): VirtualVideoFrame {
        val plane = image.planes.first()
        val width = plane.rowStride / plane.pixelStride
        val height = image.height

        val bitmap1 = obtainCachedBitmap("step1", width, height)
        bitmap1.copyPixelsFromBuffer(plane.buffer)

        val bitmap2 = obtainCachedBitmap("step2", image.width, image.height)
        bitmap2.applyCanvas {
            drawColor(Color.BLACK, PorterDuff.Mode.CLEAR)
            drawBitmap(bitmap1, 0f, 0f, null)
        }

        return VirtualVideoFrame(
            width = bitmap2.width,
            height = bitmap2.height,
            format = VirtualVideoFormat.RGBA,
            data = ByteBuffer.allocate(bitmap2.byteCount).also(bitmap2::copyPixelsToBuffer).array()
        )
    }

    private fun obtainCachedBitmap(key: String, width: Int, height: Int): Bitmap {
        var bitmap = bitmapsCache[key]
        if (bitmap != null && bitmap.width == width && bitmap.height == height) {
            return bitmap
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapsCache[key] = bitmap
        return bitmap
    }

    @Suppress("DEPRECATION")
    private fun getDisplaySize(manager: WindowManager): Point {
        return if (Build.VERSION.SDK_INT >= 30) {
            val bounds = manager.currentWindowMetrics.bounds
            Point(bounds.width(), bounds.height())
        } else {
            val displayMetrics = DisplayMetrics()
            manager.defaultDisplay.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    private class MirrorInfo(
        context: Context,
        projection: MediaProjection,
        val displaySize: Point,
        private val block: () -> Unit,
    ) : ImageReader.OnImageAvailableListener {

        val imageReader: ImageReader
        val virtualDisplay: VirtualDisplay

        init {
            val densityDpi = context.resources.configuration.densityDpi
            val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR or
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC

            @SuppressLint("WrongConstant") // nope, constant is actually correct
            imageReader = ImageReader.newInstance(
                displaySize.x,
                displaySize.y,
                PixelFormat.RGBA_8888,
                2,
            )
            imageReader.setOnImageAvailableListener(this, handler)

            virtualDisplay = projection.createVirtualDisplay(
                "connector-screen-share",
                displaySize.x,
                displaySize.y,
                densityDpi,
                flags,
                imageReader.surface,
                null,
                handler,
            )
        }

        fun release() {
            imageReader.close()
            virtualDisplay.release()
        }

        override fun onImageAvailable(reader: ImageReader?) {
            block()
        }
    }
}
