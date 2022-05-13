package com.vidyo.vidyoconnector.bl.connector.media.remote.share

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.participants.Participant
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.vidyo.VidyoClient.Endpoint.Participant as VcParticipant
import com.vidyo.VidyoClient.Device.RemoteWindowShare as VcRemoteWindowShare

class RemoteScreenShareManager(private val scope: ConnectorScope) {
    private val byId = HashMap<String, RemoteScreenShare>()
    private val byIdTrigger = MutableStateFlow(0L)
    private val onStartedEvent = MutableSharedFlow<RemoteScreenShare>(extraBufferCapacity = 64)
    private val onStoppedEvent = MutableSharedFlow<RemoteScreenShare>(extraBufferCapacity = 64)

    val onStarted = onStartedEvent.asSharedFlow()
    val onStopped = onStoppedEvent.asSharedFlow()

    init {
        scope.connector.registerRemoteWindowShareEventListener(EventListener())
    }

    private inner class EventListener : Connector.IRegisterRemoteWindowShareEventListener {
        override fun onRemoteWindowShareAdded(
            remoteWindowShare: VcRemoteWindowShare,
            participant: VcParticipant,
        ) = scope.run {
            val device = RemoteScreenShare.from(remoteWindowShare, Participant.from(participant))

            byId[device.id] = device
            byIdTrigger.trigger()
            onStartedEvent.tryEmit(device)
        }

        override fun onRemoteWindowShareRemoved(
            remoteWindowShare: VcRemoteWindowShare,
            participant: VcParticipant,
        ) = scope.run {
            val device = RemoteScreenShare.from(remoteWindowShare, Participant.from(participant))

            byId.remove(device.id)
            byIdTrigger.trigger()
            onStoppedEvent.tryEmit(device)
        }

        override fun onRemoteWindowShareStateUpdated(
            remoteWindowShare: VcRemoteWindowShare,
            participant: VcParticipant,
            state: Device.DeviceState,
        ) {
        }
    }
}
