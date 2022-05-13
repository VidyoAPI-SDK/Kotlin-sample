package com.vidyo.vidyoconnector.bl.connector.media.remote.camera

import androidx.compose.runtime.*
import com.vidyo.VidyoClient.Device.CameraPreset
import com.vidyo.vidyoconnector.bl.connector.media.base.Camera
import com.vidyo.vidyoconnector.bl.connector.media.base.CameraControlAction
import com.vidyo.vidyoconnector.bl.connector.media.base.CameraControlCapabilities
import com.vidyo.vidyoconnector.bl.connector.participants.Participant
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.utils.Loggable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.vidyo.VidyoClient.Device.RemoteCamera as VcRemoteCamera

class RemoteCamera(
    override val id: String,
    override val name: String,
    override val participantId: String,
    val handle: VcRemoteCamera,
) : Camera {
    companion object : Loggable.Tag("RemoteCamera") {
        fun from(handle: VcRemoteCamera, participantId: String) = RemoteCamera(
            id = handle.id.orEmpty(),
            name = handle.name.orEmpty(),
            participantId = participantId,
            handle = handle,
        )
    }

    private val presetsState = MutableStateFlow<List<CameraPreset>>(emptyList())
    private val controlCapabilitiesState = MutableStateFlow(CameraControlCapabilities.from(handle.controlCapabilities))

    val presets = presetsState.asStateFlow()

    override val controlCapabilities = controlCapabilitiesState.asStateFlow()

    init {
        handle.registerPresetEventListener {
            presetsState.value = it.orEmpty()
        }
    }

    override fun toString(): String {
        return "RemoteCamera(id='$id', name='$name')"
    }

    override fun controlPtzNudge(action: CameraControlAction) {
        handle.controlPTZNudge(action.panValue, action.tiltValue, action.zoomValue)
    }

    override fun controlPtzContinuousStart(action: CameraControlAction, timeout: Long) {
        handle.controlPTZStart(action.jniValue, timeout)
    }

    override fun controlPtzContinuousStop(action: CameraControlAction) {
        handle.controlPTZStop()
    }

    fun updateControlCapabilities() {
        controlCapabilitiesState.value = CameraControlCapabilities.from(handle.controlCapabilities)
    }
}

@Composable
fun collectRemoteCameraByParticipant(participant: Participant): State<RemoteCamera?> {
    if (participant.isLocal) {
        return remember { mutableStateOf(null) }
    }
    val manager = LocalConnectorManager.current.media.remoteCamera
    return remember { manager.trackByParticipantId(participant.id) }.collectAsState(null)
}
