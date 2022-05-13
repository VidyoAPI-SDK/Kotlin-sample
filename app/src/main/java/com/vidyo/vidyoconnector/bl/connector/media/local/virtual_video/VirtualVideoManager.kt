package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.VidyoClient.Device.VirtualVideoSource
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import com.vidyo.vidyoconnector.utils.logD
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

class VirtualVideoManager(private val scope: ConnectorScope) {

    companion object : Loggable.Tag("VirtualVideoManager")

    private val map = HashMap<String, VirtualVideoStream>()
    private val mapTrigger = MutableStateFlow(0L)
    private val selectedShareState = MutableStateFlow<VirtualVideoStream?>(null)
    private val selectedCameraState = MutableStateFlow<VirtualVideoStream?>(null)

    private val idCounter = AtomicLong()
    private val frameRates = HashMap<String, VirtualVideoFrameRate>()

    init {
        scope.connector.registerVirtualVideoSourceEventListener(EventListener())

        selectedCameraState.collectInScope(scope) {
            scope.connector.selectVirtualCamera(it?.source)
        }

        selectedShareState.collectInScope(scope) {
            delay(1000) // TODO IO-3999
            scope.connector.selectVirtualSourceWindowShare(it?.source)
        }

        mapTrigger.collectInScope(scope) {
            selectedCameraState.value = map.values.firstOrNull {
                it.type == VirtualVideoType.Camera
            }
            selectedShareState.value = map.values.firstOrNull {
                it.type == VirtualVideoType.ScreenShare
            }
        }
    }

    suspend fun createStream(type: VirtualVideoType, frameRate: VirtualVideoFrameRate) = withContext(NonCancellable) {
        logD { "createStream: type = $type, frameRate = $frameRate" }

        val id = idCounter.incrementAndGet().toString()
        val name = "${type.name}_$id"

        frameRates[id] = frameRate
        try {
            if (!scope.connector.createVirtualVideoSource(type.jniValue, id, name)) {
                error("createVirtualVideoSource failed")
            }
            mapTrigger.mapNotNull { map[id] }.first()
        } finally {
            frameRates.remove(id)
        }
    }

    private inner class EventListener : Connector.IRegisterVirtualVideoSourceEventListener {
        override fun onVirtualVideoSourceAdded(virtualVideoSource: VirtualVideoSource) {
            logD { "onVirtualVideoSourceAdded: id = ${virtualVideoSource.id}" }
            scope.run {
                val id = virtualVideoSource.id.orEmpty()
                val stream = VirtualVideoStream(
                    scope = scope,
                    source = virtualVideoSource,
                    frameRate = frameRates[id] ?: VirtualVideoFrameRate.Default,
                )
                map[id] = stream
                mapTrigger.trigger()
            }
        }

        override fun onVirtualVideoSourceRemoved(virtualVideoSource: VirtualVideoSource) {
            logD { "onVirtualVideoSourceRemoved: id = ${virtualVideoSource.id}" }
            scope.run {
                map.remove(virtualVideoSource.id.orEmpty())
                mapTrigger.trigger()
            }
        }

        override fun onVirtualVideoSourceStateUpdated(
            virtualVideoSource: VirtualVideoSource,
            state: Device.DeviceState,
        ) {
            logD { "onVirtualVideoSourceStateUpdated: id = ${virtualVideoSource.id}, state = $state" }
        }

        override fun onVirtualVideoSourceExternalMediaBufferReleased(
            virtualVideoSource: VirtualVideoSource,
            buffer: ByteArray,
            size: Long,
        ) {
        }
    }
}
