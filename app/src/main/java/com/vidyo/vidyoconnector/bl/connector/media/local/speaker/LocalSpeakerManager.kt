package com.vidyo.vidyoconnector.bl.connector.media.local.speaker

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.media.RecordingsManager
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.collectInScopeLatest
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import com.vidyo.vidyoconnector.utils.logD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import com.vidyo.VidyoClient.Device.LocalSpeaker as VcLocalSpeaker

class LocalSpeakerManager(private val scope: ConnectorScope) {
    companion object : Loggable.Tag("LocalSpeakerManager")

    private val map = LinkedHashMap<String, LocalSpeaker>()
    private val mapTrigger = MutableStateFlow(0L)
    private val list = MutableStateFlow(emptyList<LocalSpeaker>())
    private val selectedState = MutableStateFlow(LocalSpeaker.Null)
    private val mutedState = MutableStateFlow(false)

    val all = list.asStateFlow()
    val selected = selectedState.asStateFlow()
    val muted = mutedState.asStateFlow()
    val record = MutableStateFlow(false)

    init {
        scope.connector.registerLocalSpeakerEventListener(EventListener())
        scope.connector.selectDefaultSpeaker()

        mapTrigger.debounce(500).collectInScope(scope) {
            list.value = map.values.toList()
        }

        selected.collectInScope(scope) {
            logD { "selected = $it" }
        }

        selected.collectInScopeLatest(scope) {
            try {
                record.collect { record ->
                    it.setDebugRecordingsPath(RecordingsManager.recordFolder.takeIf { record })
                }
            } finally {
                it.setDebugRecordingsPath(null)
            }
        }
    }

    fun selectDevice(device: LocalSpeaker) {
        scope.connector.selectLocalSpeaker(device.handle)
    }

    fun requestMutedState(muted: Boolean) {
        if (scope.connector.setSpeakerPrivacy(muted)) {
            mutedState.value = muted
        }
    }

    private inner class EventListener : Connector.IRegisterLocalSpeakerEventListener {
        override fun onLocalSpeakerAdded(localSpeaker: VcLocalSpeaker) {
            scope.run {
                val device = LocalSpeaker.from(localSpeaker)
                map[device.id] = device
                mapTrigger.trigger()
            }
        }

        override fun onLocalSpeakerRemoved(localSpeaker: VcLocalSpeaker) {
            scope.run {
                map.remove(localSpeaker.id.orEmpty())
                mapTrigger.trigger()
            }
        }

        override fun onLocalSpeakerSelected(localSpeaker: VcLocalSpeaker?) {
            scope.run {
                selectedState.value = map[localSpeaker?.id.orEmpty()] ?: LocalSpeaker.Null
            }
        }

        override fun onLocalSpeakerStateUpdated(
            localSpeaker: VcLocalSpeaker,
            state: Device.DeviceState,
        ) {
        }
    }
}
