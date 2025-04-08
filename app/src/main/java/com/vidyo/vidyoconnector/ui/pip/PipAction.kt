package com.vidyo.vidyoconnector.ui.pip

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.vidyo.vidyoconnector.R
/***
 * @author Saurabh.jain
 * @property iconResId Int
 * @property titleResId Int
 * @property controlType Int
 * @constructor
 */
sealed class PipAction(@DrawableRes val iconResId: Int, @StringRes val titleResId: Int, @PipControlType val controlType: Int){
    object MicrophoneMute :PipAction(R.drawable.ic_microphone_off, R.string.CONFERENCE__contentdesc_mic_muted, CONTROL_TYPE_MICROPHONE_MUTE)
    object MicrophoneUnMute :PipAction(R.drawable.ic_microphone_on, R.string.CONFERENCE__contentdesc_mic_unmuted, CONTROL_TYPE_MICROPHONE_UN_MUTE)
    object CameraMute :PipAction(R.drawable.ic_camera_off, R.string.CONFERENCE__contentdesc_camera_muted, CONTROL_TYPE_CAMERA_MUTE)
    object CameraUnMute :PipAction(R.drawable.ic_camera_on, R.string.CONFERENCE__contentdesc_camera_unmuted, CONTROL_TYPE_CAMERA_UN_MUTE)
    object EndCall :PipAction(R.drawable.ic_call_end_24, R.string.CONFERENCESERVICE__notification_end_call, CONTROL_TYPE_END_CALL)
}

/*
sealed class PipAction(@DrawableRes val iconResId: Int, @StringRes val titleResId: Int, @PipControlType val controlType: Int){
    data object MicrophoneMute :PipAction(R.drawable.ic_mic_mute_24, R.string.CONFERENCE__contentdesc_mic_muted, CONTROL_TYPE_MICROPHONE_MUTE)
    data object MicrophoneUnMute :PipAction(R.drawable.ic_mic_unmute_24, R.string.CONFERENCE__contentdesc_mic_unmuted, CONTROL_TYPE_MICROPHONE_UN_MUTE)
    data object CameraMute :PipAction(R.drawable.ic_camera_mute_24, R.string.CONFERENCE__contentdesc_camera_muted, CONTROL_TYPE_CAMERA_MUTE)
    data object CameraUnMute :PipAction(R.drawable.ic_camera_unmute_24, R.string.CONFERENCE__contentdesc_camera_unmuted, CONTROL_TYPE_CAMERA_UN_MUTE)
    data object EndCall :PipAction(R.drawable.ic_call_end_24, R.string.CONFERENCESERVICE__notification_end_call, CONTROL_TYPE_END_CALL)
}*/
