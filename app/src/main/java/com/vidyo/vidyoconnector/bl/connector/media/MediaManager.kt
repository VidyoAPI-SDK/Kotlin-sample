package com.vidyo.vidyoconnector.bl.connector.media

import com.vidyo.VidyoClient.Connector.Connector
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
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaManager(
    scope: ConnectorScope,
    conference: ConferenceManager,
    preferences: PreferencesManager,
) {
    private val virtualVideo = VirtualVideoManager(scope)
    private val audioOnlyState = MutableStateFlow(false)

    val audioOnly = audioOnlyState.asStateFlow()
    val recordings = RecordingsManager(scope)

    val localCamera = LocalCameraManager(scope, preferences)
    val localSpeaker = LocalSpeakerManager(scope)
    val localMicrophone = LocalMicrophoneManager(scope)
    val localScreenShare = LocalScreenShareManager(scope, conference, virtualVideo)

    val remoteCamera = RemoteCameraManager(scope)
    val remoteMicrophone = RemoteMicrophoneManager(scope)
    val remoteScreenShare = RemoteScreenShareManager(scope)

    init {
        scope.connector.registerResourceManagerEventListener(ResourceManagerEvents())

        conference.state.collectInScope(scope) {
            if (!it.isActive) audioOnlyState.value = false
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
}
