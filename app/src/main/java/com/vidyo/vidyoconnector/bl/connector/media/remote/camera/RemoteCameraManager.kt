package com.vidyo.vidyoconnector.bl.connector.media.remote.camera

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.VidyoClient.Endpoint.Participant
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import com.vidyo.vidyoconnector.utils.logD
import kotlinx.coroutines.flow.*
import com.vidyo.VidyoClient.Device.RemoteCamera as VcRemoteCamera

class RemoteCameraManager(private val scope: ConnectorScope) {
    companion object : Loggable.Tag("RemoteCameraManager")

    private val byId = HashMap<String, RemoteCamera>()
    private val byParticipantId = HashMap<String, HashSet<RemoteCamera>>()
    private val trigger = MutableStateFlow(0L)
    private val allState = MutableStateFlow(emptyList<RemoteCamera>())

    val all = allState.asStateFlow()

    init {
        scope.connector.registerRemoteCameraEventListener(EventListener())

        trigger.debounce(500).collectInScope(scope) {
            allState.value = byId.values.toList()
        }
    }

    fun trackByParticipantId(id: String): Flow<RemoteCamera?> {
        return trigger.map { byParticipantId[id]?.firstOrNull() }.distinctUntilChanged()
    }

    private inner class EventListener : Connector.IRegisterRemoteCameraEventListener {
        override fun onRemoteCameraAdded(remoteCamera: VcRemoteCamera, participant: Participant) {
            scope.run {
                val participantId = participant.id.orEmpty()
                val device = RemoteCamera.from(remoteCamera, participantId)

                logD { "cameraAdded: device = $device" }

                byId[device.id] = device
                byParticipantId.getOrPut(participantId) { HashSet() }.add(device)

                trigger.trigger()
            }
        }

        override fun onRemoteCameraRemoved(remoteCamera: VcRemoteCamera, participant: Participant) {
            scope.run {
                val deviceId = remoteCamera.id.orEmpty()
                val participantId = participant.id.orEmpty()

                val device = byId.remove(deviceId)

                logD { "cameraRemoved: device = $device" }

                val map = byParticipantId[participantId]
                if (map?.removeAll { it.id == deviceId } == true && map.isEmpty()) {
                    byParticipantId.remove(participantId)
                }

                trigger.trigger()
            }
        }

        override fun onRemoteCameraStateUpdated(
            remoteCamera: VcRemoteCamera,
            participant: Participant,
            state: Device.DeviceState
        ) {
            val deviceId = remoteCamera.id.orEmpty()
            val device = byId[deviceId] ?: return

            scope.run {
                when (state) {
                    Device.DeviceState.VIDYO_DEVICESTATE_Controllable,
                    Device.DeviceState.VIDYO_DEVICESTATE_NotControllable -> {
                        device.updateControlCapabilities()
                    }
                    else -> Unit
                }
            }
        }
    }
}
