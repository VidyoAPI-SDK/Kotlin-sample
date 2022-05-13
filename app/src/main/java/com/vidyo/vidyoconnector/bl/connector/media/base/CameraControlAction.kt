package com.vidyo.vidyoconnector.bl.connector.media.base

import com.vidyo.VidyoClient.Device.CameraControlCapabilities.CameraControlDirection as VcCameraControlDirection

sealed interface CameraControlAction {

    val panValue: Int
    val tiltValue: Int
    val zoomValue: Int
    val jniValue: VcCameraControlDirection

    fun hasNudgeMode(capabilities: CameraControlCapabilities): Boolean
    fun hasContinuousMode(capabilities: CameraControlCapabilities): Boolean

    // groups

    sealed class PanTilt : CameraControlAction {
        override fun hasNudgeMode(capabilities: CameraControlCapabilities): Boolean {
            return capabilities.panTiltHasNudge
        }

        override fun hasContinuousMode(capabilities: CameraControlCapabilities): Boolean {
            return capabilities.panTiltHasContinuousMove
        }
    }

    sealed class Zoom : CameraControlAction {
        override fun hasNudgeMode(capabilities: CameraControlCapabilities): Boolean {
            return capabilities.zoomHasNudge
        }

        override fun hasContinuousMode(capabilities: CameraControlCapabilities): Boolean {
            return capabilities.zoomHasContinuousMove
        }
    }

    // implementations

    object PanLeft : PanTilt() {
        override val panValue = -1
        override val tiltValue = 0
        override val zoomValue = 0
        override val jniValue = VcCameraControlDirection.VIDYO_CAMERACONTROLDIRECTION_PanLeft
    }

    object PanRight : PanTilt() {
        override val panValue = 1
        override val tiltValue = 0
        override val zoomValue = 0
        override val jniValue = VcCameraControlDirection.VIDYO_CAMERACONTROLDIRECTION_PanRight
    }

    object TiltUp : PanTilt() {
        override val panValue = 0
        override val tiltValue = 1
        override val zoomValue = 0
        override val jniValue = VcCameraControlDirection.VIDYO_CAMERACONTROLDIRECTION_TiltUp
    }

    object TiltDown : PanTilt() {
        override val panValue = 0
        override val tiltValue = -1
        override val zoomValue = 0
        override val jniValue = VcCameraControlDirection.VIDYO_CAMERACONTROLDIRECTION_TiltDown
    }

    object ZoomIn : Zoom() {
        override val panValue = 0
        override val tiltValue = 0
        override val zoomValue = 1
        override val jniValue = VcCameraControlDirection.VIDYO_CAMERACONTROLDIRECTION_ZoomIn
    }

    object ZoomOut : Zoom() {
        override val panValue = 0
        override val tiltValue = 0
        override val zoomValue = -1
        override val jniValue = VcCameraControlDirection.VIDYO_CAMERACONTROLDIRECTION_ZoomOut
    }
}
