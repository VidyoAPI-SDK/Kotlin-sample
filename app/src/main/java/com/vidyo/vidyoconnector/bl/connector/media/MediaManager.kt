package com.vidyo.vidyoconnector.bl.connector.media

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.VidyoClient.Endpoint.Room
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import com.vidyo.vidyoconnector.bl.connector.media.local.camera.LocalCameraManager
import com.vidyo.vidyoconnector.bl.connector.media.local.microphone.LocalMicrophoneManager
import com.vidyo.vidyoconnector.bl.connector.media.local.screen_share.LocalScreenShareManager
import com.vidyo.vidyoconnector.bl.connector.media.local.speaker.LocalSpeakerManager
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoManager
import com.vidyo.vidyoconnector.bl.connector.media.remote.camera.RemoteCameraManager
import com.vidyo.vidyoconnector.bl.connector.media.remote.microphone.RemoteMicrophoneManager
import com.vidyo.vidyoconnector.bl.connector.media.remote.share.RemoteScreenShareManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaManager(scope: ConnectorScope, conference: ConferenceManager) {
    private val virtualVideo = VirtualVideoManager(scope)
    private val audioOnlyState = MutableStateFlow(false)
    private val localCameraModeration = MutableStateFlow(MutedState.None)
    private val localMicrophoneModeration = MutableStateFlow(MutedState.None)

    val audioOnly = audioOnlyState.asStateFlow()
    val recordings = RecordingsManager(scope)

    val localCamera = LocalCameraManager(scope, localCameraModeration)
    val localSpeaker = LocalSpeakerManager(scope)
    val localMicrophone = LocalMicrophoneManager(scope, localMicrophoneModeration)
    val localScreenShare = LocalScreenShareManager(scope, conference, virtualVideo)

    val remoteCamera = RemoteCameraManager(scope)
    val remoteMicrophone = RemoteMicrophoneManager(scope)
    val remoteScreenShare = RemoteScreenShareManager(scope)

    init {
        scope.connector.registerResourceManagerEventListener(ResourceManagerEvents())
        scope.connector.registerModerationCommandEventListener(ModerationCommandEventListener())

        conference.conference.collectInScope(scope) {
            if (!it.state.isActive) {
                audioOnlyState.value = false
                localCameraModeration.value = MutedState.None
                localMicrophoneModeration.value = MutedState.None
            }
        }
    }

    private inner class ResourceManagerEvents : Connector.IRegisterResourceManagerEventListener {
        override fun onAvailableResourcesChanged(
            cpuEncode: Int,
            cpuDecode: Int,
            bandwidthSend: Int,
            bandwidthReceive: Int
        ) = Unit

        override fun onMaxRemoteSourcesChanged(maxRemoteSources: Int) {
            audioOnlyState.value = (maxRemoteSources == 0)
        }
    }

    private inner class ModerationCommandEventListener : Connector.IRegisterModerationCommandEventListener {
        override fun onModerationCommandReceived(
            deviceType: Device.DeviceType?,
            moderationType: Room.RoomModerationType?,
            muted: Boolean,
        ) {
            val forces = (moderationType == Room.RoomModerationType.VIDYO_ROOMMODERATIONTYPE_HardMute)
            val state = when {
                !muted -> MutedState.None
                forces -> MutedState.ForceMuted
                else -> MutedState.Muted
            }

            when (deviceType) {
                Device.DeviceType.VIDYO_DEVICETYPE_LocalCamera -> {
                    localCameraModeration.value = state
                }
                Device.DeviceType.VIDYO_DEVICETYPE_LocalMicrophone -> {
                    localMicrophoneModeration.value = state
                }
                else -> Unit
            }
        }
    }
}
