package com.vidyo.vidyoconnector.bl.connector.media.local.camera

import com.vidyo.vidyoconnector.bl.connector.media.base.Camera
import com.vidyo.vidyoconnector.bl.connector.media.base.CameraControlAction
import com.vidyo.vidyoconnector.bl.connector.media.base.CameraControlCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.vidyo.VidyoClient.Device.LocalCamera as VcLocalCamera

data class LocalCamera(
    override val id: String,
    override val name: String,
    val handle: VcLocalCamera,
) : Camera {
    companion object {
        fun from(handle: VcLocalCamera) = LocalCamera(
            id = handle.id.orEmpty(),
            name = handle.name.orEmpty(),
            handle = handle,
        )
    }

    private val controlCapabilitiesState = MutableStateFlow(CameraControlCapabilities.from(handle.controlCapabilities))

    val constraints = LocalCameraConstraints.from(this)

    override val participantId = ""
    override val controlCapabilities = controlCapabilitiesState.asStateFlow()

    override fun controlPtzNudge(action: CameraControlAction) {
        handle.controlPTZ(action.panValue, action.tiltValue, action.zoomValue)
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
