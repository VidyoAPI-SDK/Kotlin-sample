package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import android.os.SystemClock
import com.vidyo.VidyoClient.Device.VirtualVideoSource
import com.vidyo.VidyoClient.Endpoint.MediaFormat
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScopeLatest
import com.vidyo.vidyoconnector.utils.logD
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.nanoseconds

class VirtualVideoStream(
    private val scope: ConnectorScope,
    val source: VirtualVideoSource,
    private val frameRate: VirtualVideoFrameRate,
) {
    companion object : Loggable.Tag("VirtualVideoStream")

    private var lastConstraintsWidth = 0
    private var lastConstraintsHeight = 0
    private var lastFrameTimeNanos = 0L
    private var minimalFrameIntervalNanos = 0L

    private val processingJob: Job
    private val activeState = MutableStateFlow(false)
    private val frameChannel = Channel<VirtualVideoFrame>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val id = source.id.orEmpty()
    val name = source.name.orEmpty()
    val type = VirtualVideoType.fromJniValue(source.type)

    init {
        logD { "create: id = $id, name = $name, type = $type" }

        source.registerEventListener(EventListener())

        processingJob = activeState.collectInScopeLatest(scope) {
            if (it) doProcessingLoop()
        }
    }

    fun destroy() {
        logD { "destroy: id = $id" }

        processingJob.cancel()
        scope.connector.destroyVirtualVideoSource(source)
    }

    override fun toString() = name
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is VirtualVideoStream && other.id == id

    suspend fun sendFrame(frame: VirtualVideoFrame) {
        frameChannel.send(frame)
    }

    private suspend fun doProcessingLoop() {
        while (true) {
            processFrame(frameChannel.receive())

            val time = SystemClock.elapsedRealtimeNanos()
            delay((minimalFrameIntervalNanos + lastFrameTimeNanos - time).nanoseconds)
            lastFrameTimeNanos = time
        }
    }

    private fun processFrame(frame: VirtualVideoFrame) {
        if (lastConstraintsWidth != frame.width || lastConstraintsHeight != frame.height) {
            lastConstraintsWidth = frame.width
            lastConstraintsHeight = frame.height

            val maxScale = type.computeScaleMax(frame.width, frame.height)

            source.setMaxConstraints(
                (frame.width * maxScale).roundToInt(),
                (frame.height * maxScale).roundToInt(),
                frameRate.intervalMin.inWholeNanoseconds
            )

            logD { "processFrame: reconfigure, width = ${frame.width}, height = ${frame.height}" }
        }

        source.onFrame(frame.toVideoFrame(), frame.format.sdkValue)
    }

    private inner class EventListener : VirtualVideoSource.IRegisterEventListener {
        override fun startCallback(frameInterval: Long, mediaFormat: MediaFormat, userData: Long) {
            logD { "startCallback" }
            minimalFrameIntervalNanos = frameInterval
            activeState.value = true
        }

        override fun reconfigureCallback(
            frameInterval: Long,
            mediaFormat: MediaFormat,
            userData: Long
        ) {
            minimalFrameIntervalNanos = frameInterval
        }

        override fun externalMediaBufferReleaseCallback(
            bytes: ByteArray,
            size: Long,
            userData: Long
        ) {
        }

        override fun stopCallback(userData: Long) {
            logD { "stopCallback" }
            activeState.value = false
        }
    }
}
