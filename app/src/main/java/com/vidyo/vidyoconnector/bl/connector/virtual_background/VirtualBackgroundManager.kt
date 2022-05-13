package com.vidyo.vidyoconnector.bl.connector.virtual_background

import androidx.compose.runtime.*
import com.banuba.sdk.utils.ContextProvider
import com.banuba.utils.FileUtilsNN
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Connector.ConnectorCameraEffectInfo
import com.vidyo.prepareBnbResources
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.withPrevious
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

class VirtualBackgroundManager(private val scope: ConnectorScope) {
    private val sha1 by lazy { MessageDigest.getInstance("SHA-1") }
    private val resources = scope.async(Dispatchers.Default) {
        prepareBnbResources(scope.context)
    }
    private val effectState = MutableStateFlow<VirtualBackgroundEffect>(
        VirtualBackgroundEffect.None
    )

    val effect = effectState.asStateFlow()

    init {
        FileUtilsNN.setContext(scope.context)
        ContextProvider.setContext(scope.context)

        fun VirtualBackgroundEffect.isVirtualBackground(): Boolean {
            return jniValue == Connector.ConnectorCameraEffectType.VIDYO_CONNECTORCAMERAEFFECTTYPE_VirtualBackground
        }

        effectState.withPrevious(VirtualBackgroundEffect.None).collectInScope(scope) {
            if (it.old.isVirtualBackground() && it.new.isVirtualBackground()) {
                setEffectAsync(VirtualBackgroundEffect.Blur)
            }
            if (!setEffectAsync(it.new)) {
                effectState.value = VirtualBackgroundEffect.None
            }
        }
    }

    fun setEffect(effect: VirtualBackgroundEffect) {
        effectState.value = effect
    }

    private suspend fun setEffectAsync(effect: VirtualBackgroundEffect): Boolean {
        val resources = resources.await() ?: return false

        val effectInfo = ConnectorCameraEffectInfo()
        effectInfo.token = BnbLicenseToken.BNB_TOKEN
        effectInfo.effectType = effect.jniValue
        effectInfo.pathToResources = resources.root.absolutePath

        when (effect) {
            is VirtualBackgroundEffect.None -> {
            }
            is VirtualBackgroundEffect.Blur -> {
                effectInfo.pathToEffect = resources.backgroundBlurEffect.absolutePath
                effectInfo.blurIntensity = effect.radius
            }
            is VirtualBackgroundEffect.UriImage -> {
                val path = prepareImageFromStream(effect.uri) {
                    scope.context.contentResolver.openInputStream(effect.uri)
                } ?: return false

                effectInfo.pathToEffect = resources.virtualBackgroundEffect.absolutePath
                effectInfo.virtualBackgroundPicture = path.absolutePath
            }
            is VirtualBackgroundEffect.AssetImage -> {
                val path = prepareImageFromStream(effect.path) {
                    scope.context.resources.assets.open(effect.path)
                } ?: return false

                effectInfo.pathToEffect = resources.virtualBackgroundEffect.absolutePath
                effectInfo.virtualBackgroundPicture = path.absolutePath
            }
        }

        return scope.connector.setCameraBackgroundEffect(effectInfo)
    }

    private suspend fun prepareImageFromStream(key: Any, block: () -> InputStream?): File? {
        try {
            return withContext(Dispatchers.IO) {
                val name = sha1.digest(key.toString().toByteArray())
                val file = File(scope.context.cacheDir, "virtual-bg-$name.png")

                if (file.exists()) {
                    return@withContext file
                }

                @Suppress("BlockingMethodInNonBlockingContext")
                block().use { input ->
                    if (input == null) {
                        return@withContext null
                    }
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                return@withContext file
            }
        } catch (e: Exception) {
            return null
        }
    }
}
