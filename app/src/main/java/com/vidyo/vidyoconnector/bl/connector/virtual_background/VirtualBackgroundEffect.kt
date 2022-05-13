package com.vidyo.vidyoconnector.bl.connector.virtual_background

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vidyo.VidyoClient.Connector.Connector.ConnectorCameraEffectType
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

sealed interface VirtualBackgroundEffect {
    val textId: Int
    val jniValue: ConnectorCameraEffectType

    @Composable
    fun previewPainter(): State<Painter>

    // implementations

    object None : VirtualBackgroundEffect {
        override val textId = R.string.VirtualBackgroundEffect_None
        override val jniValue = ConnectorCameraEffectType.VIDYO_CONNECTORCAMERAEFFECTTYPE_None

        @Composable
        override fun previewPainter(): State<Painter> {
            val resource = painterResource(R.drawable.virtual_bg_none_preview)
            return remember { mutableStateOf(resource) }
        }
    }

    object Blur : VirtualBackgroundEffect {
        const val radius = 5

        override val textId = R.string.VirtualBackgroundEffect_Blur
        override val jniValue = ConnectorCameraEffectType.VIDYO_CONNECTORCAMERAEFFECTTYPE_Blur

        @Composable
        override fun previewPainter(): State<Painter> {
            val resource = painterResource(R.drawable.virtual_bg_blur_preview)
            return remember { mutableStateOf(resource) }
        }
    }

    data class UriImage(val uri: Uri) : VirtualBackgroundEffect {
        companion object {
            val Preview = UriImage(Uri.EMPTY)
        }

        override val textId = R.string.VirtualBackgroundEffect_FileImage
        override val jniValue =
            ConnectorCameraEffectType.VIDYO_CONNECTORCAMERAEFFECTTYPE_VirtualBackground

        @Composable
        override fun previewPainter(): State<Painter> {
            if (uri == Uri.EMPTY) {
                val resource = painterResource(R.drawable.virtual_bg_file_preview)
                return remember { mutableStateOf(resource) }
            }
            val context = LocalContext.current
            return collectPreviewAsState(uri) {
                context.contentResolver.openInputStream(uri)
            }
        }
    }

    data class AssetImage(val path: String) : VirtualBackgroundEffect {
        companion object {
            val All = loadAssetImages()
        }

        override val textId = R.string.VirtualBackgroundEffect_AssetImage
        override val jniValue =
            ConnectorCameraEffectType.VIDYO_CONNECTORCAMERAEFFECTTYPE_VirtualBackground

        @Composable
        override fun previewPainter(): State<Painter> {
            val context = LocalContext.current
            return collectPreviewAsState(path) {
                context.resources.assets.open(path)
            }
        }
    }
}

private fun loadAssetImages(): List<VirtualBackgroundEffect.AssetImage> {
    val root = "virtual_background_effects"
    return appContext.resources.assets.list(root).orEmpty().map {
        VirtualBackgroundEffect.AssetImage("$root/$it")
    }
}

@Composable
private fun collectPreviewAsState(key: Any, block: () -> InputStream?): State<Painter> {
    val painter = remember(key) {
        mutableStateOf<Painter>(ColorPainter(Color.Transparent))
    }

    val density = LocalDensity.current

    fun loadBitmap(options: BitmapFactory.Options) = try {
        block()?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    } catch (e: Exception) {
        null
    }

    LaunchedEffect(key) {
        val bitmap = withContext(Dispatchers.IO) {
            val options = BitmapFactory.Options().also {
                it.inJustDecodeBounds = true
            }

            loadBitmap(options)

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                return@withContext null
            }

            yield()

            val expectedSize = with(density) { 120.dp.toPx() }
            val actualSize = max(options.outWidth, options.outHeight)
            val samples = actualSize / expectedSize

            options.inJustDecodeBounds = false
            options.inSampleSize = samples.roundToInt().coerceAtLeast(1)

            loadBitmap(options)
        }

        if (bitmap != null) {
            painter.value = BitmapPainter(bitmap.asImageBitmap())
        }
    }

    return painter
}
