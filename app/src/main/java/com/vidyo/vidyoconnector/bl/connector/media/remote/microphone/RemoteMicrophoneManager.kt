package com.vidyo.vidyoconnector.bl.connector.media.remote.microphone

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.VidyoClient.Endpoint.Participant
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.vidyo.VidyoClient.Device.RemoteMicrophone as VcRemoteMicrophone

class RemoteMicrophoneManager(private val scope: ConnectorScope) {
    private val byId = HashMap<String, RemoteMicrophone>()
    private val byParticipantId = HashMap<String, HashSet<String>>()
    private val trigger = MutableStateFlow(0L)

    init {
        scope.connector.registerRemoteMicrophoneEventListener(EventListener())
    }

    fun trackParticipantAvailability(id: String): Flow<Boolean> {
        return trigger.map { byParticipantId.containsKey(id) }.distinctUntilChanged()
    }

    private inner class EventListener : Connector.IRegisterRemoteMicrophoneEventListener {
        override fun onRemoteMicrophoneAdded(
            remoteMicrophone: VcRemoteMicrophone,
            participant: Participant,
        ) = Unit

        override fun onRemoteMicrophoneRemoved(
            remoteMicrophone: VcRemoteMicrophone,
            participant: Participant,
        ) {
            removeDevice(remoteMicrophone, participant)
        }

        override fun onRemoteMicrophoneStateUpdated(
            remoteMicrophone: VcRemoteMicrophone,
            participant: Participant,
            state: Device.DeviceState,
        ) {
            when (state) {
                Device.DeviceState.VIDYO_DEVICESTATE_Resumed -> {
                    addDevice(remoteMicrophone, participant)
                }
                Device.DeviceState.VIDYO_DEVICESTATE_Paused -> {
                    removeDevice(remoteMicrophone, participant)
                }
                else -> Unit
            }
        }

        fun addDevice(remoteMicrophone: VcRemoteMicrophone, participant: Participant) {
            scope.run {
                val participantId = participant.id.orEmpty()
                val device = RemoteMicrophone.from(remoteMicrophone, participantId)

                byId[device.id] = device
                byParticipantId.getOrPut(participantId) { HashSet() }.add(device.id)

                trigger.trigger()
            }
        }

        fun removeDevice(remoteMicrophone: VcRemoteMicrophone, participant: Participant) {
            scope.run {
                val deviceId = remoteMicrophone.id.orEmpty()
                val participantId = participant.id.orEmpty()

                byId.remove(deviceId)

                val map = byParticipantId[participantId]
                if (map?.remove(deviceId) == true && map.isEmpty()) {
                    byParticipantId.remove(participantId)
                }

                trigger.trigger()
            }
        }
    }
}
