package com.vidyo.vidyoconnector.bl.connector.media.base

import com.vidyo.VidyoClient.Device.CameraControlCapabilities as VcCameraControlCapabilities

data class CameraControlCapabilities(
    val hasPresets: Boolean = false,
    val panTiltHasNudge: Boolean = false,
    val panTiltHasRubberBand: Boolean = false,
    val panTiltHasContinuousMove: Boolean = false,
    val zoomHasNudge: Boolean = false,
    val zoomHasRubberBand: Boolean = false,
    val zoomHasContinuousMove: Boolean = false,
) {
    companion object {
        fun from(value: VcCameraControlCapabilities?): CameraControlCapabilities {
            value ?: return CameraControlCapabilities()
            return CameraControlCapabilities(
                hasPresets = value.hasPresetSupport,
                panTiltHasNudge = value.panTiltHasNudge,
                panTiltHasRubberBand = value.panTiltHasRubberBand,
                panTiltHasContinuousMove = value.panTiltHasContinuousMove,
                zoomHasNudge = value.zoomHasNudge,
                zoomHasRubberBand = value.zoomHasRubberBand,
                zoomHasContinuousMove = value.zooomHasContinuousMove,
            )
        }
    }

    val hasZoom = zoomHasNudge || zoomHasContinuousMove
    val hasPanTilt = panTiltHasNudge || panTiltHasContinuousMove
    val hasAny = hasZoom || hasPanTilt
}
