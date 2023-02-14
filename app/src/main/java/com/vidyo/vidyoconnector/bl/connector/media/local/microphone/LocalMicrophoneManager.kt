package com.vidyo.vidyoconnector.bl.connector.media.local.microphone

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.media.MutedState
import com.vidyo.vidyoconnector.bl.connector.media.RecordingsManager
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.collectInScopeLatest
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import com.vidyo.vidyoconnector.utils.logD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import com.vidyo.VidyoClient.Device.LocalMicrophone as VcLocalMicrophone

class LocalMicrophoneManager(private val scope: ConnectorScope, moderation: MutableStateFlow<MutedState>) {
    companion object : Loggable.Tag("LocalMicrophoneManager")

    private val map = HashMap<String, LocalMicrophone>()
    private val mapTrigger = MutableStateFlow(0L)
    private val list = MutableStateFlow(emptyList<LocalMicrophone>())
    private val selectedState = MutableStateFlow(LocalMicrophone.Null)
    private val mutedState = MutableStateFlow(MutedState.None)

    val all = list.asStateFlow()
    val selected = selectedState.asStateFlow()
    val muted = mutedState.asStateFlow()
    val record = MutableStateFlow(false)

    init {
        scope.connector.registerLocalMicrophoneEventListener(EventListener())
        scope.connector.selectDefaultMicrophone()

        moderation.collectInScope(scope) {
            val state = when (it) {
                MutedState.None -> when (mutedState.value.muted) {
                    true -> MutedState.Muted
                    else -> MutedState.None
                }
                MutedState.Muted -> MutedState.Muted
                MutedState.ForceMuted -> MutedState.ForceMuted
            }
            mutedState.value = state
        }

        mapTrigger.debounce(500).collectInScope(scope) {
            val temp = map.values.toMutableList()
            temp.sortBy { it.name }
            list.value = temp
        }

        muted.collectInScope(scope) {
            logD { "muted = $it" }
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

    fun selectDevice(device: LocalMicrophone) {
        scope.connector.selectLocalMicrophone(device.handle)
    }

    fun requestMutedState(muted: Boolean) {
        if (scope.connector.setMicrophonePrivacy(muted)) {
            mutedState.value = when (muted) {
                true -> MutedState.Muted
                else -> MutedState.None
            }
        }
    }

    private inner class EventListener : Connector.IRegisterLocalMicrophoneEventListener {
        override fun onLocalMicrophoneAdded(localMicrophone: VcLocalMicrophone) {
            scope.run {
                val device = LocalMicrophone.from(localMicrophone)
                map[device.id] = device
                mapTrigger.trigger()
            }
        }

        override fun onLocalMicrophoneRemoved(localMicrophone: VcLocalMicrophone) {
            scope.run {
                map.remove(localMicrophone.id.orEmpty())
                mapTrigger.trigger()
            }
        }

        override fun onLocalMicrophoneSelected(localMicrophone: VcLocalMicrophone?) {
            scope.run {
                selectedState.value = map[localMicrophone?.id.orEmpty()] ?: LocalMicrophone.Null
            }
        }

        override fun onLocalMicrophoneStateUpdated(
            localMicrophone: VcLocalMicrophone,
            state: Device.DeviceState
        ) {
        }
    }
}
