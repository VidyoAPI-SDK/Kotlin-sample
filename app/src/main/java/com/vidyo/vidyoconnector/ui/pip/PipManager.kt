package com.vidyo.vidyoconnector.ui.pip

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.logD

/** Intent action for pip controls from Picture-in-Picture mode.  */
const val ACTION_PIP_CONTROL = "pip_control"

/** Intent extra for pip controls from Picture-in-Picture mode.  */
const val EXTRA_CONTROL_TYPE = "control_type"
const val CONTROL_TYPE_MICROPHONE_MUTE = 1
const val CONTROL_TYPE_MICROPHONE_UN_MUTE = 2
const val CONTROL_TYPE_CAMERA_MUTE = 3
const val CONTROL_TYPE_CAMERA_UN_MUTE = 4
const val CONTROL_TYPE_END_CALL = 5

@IntDef(
    CONTROL_TYPE_MICROPHONE_MUTE,
    CONTROL_TYPE_MICROPHONE_UN_MUTE,
    CONTROL_TYPE_CAMERA_MUTE,
    CONTROL_TYPE_CAMERA_UN_MUTE,
    CONTROL_TYPE_END_CALL
)
@Retention(
    AnnotationRetention.SOURCE
)
annotation class PipControlType

/**
 * @author Saurabh.jain
 * Manager to control Picture in Picture mode support
 * @constructor
 */
class PipManager(private val context :Context) {

    companion object : Loggable {
        override val logTag = "PipManager"
    }

    private val pipRational = Rational(16, 9)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDefaultPipParams(): PictureInPictureParams {
        logD { "$logTag, getDefaultPipParams"}

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PictureInPictureParams.Builder()
                .setAspectRatio(pipRational)
                .setAutoEnterEnabled(false)
                .setSeamlessResizeEnabled(false)
                .build()
        } else {
            PictureInPictureParams.Builder()
                .setAspectRatio(pipRational)
                .build()
        }
    }

    /**
     * Build PIP params with basic requirement
     * @param isMicroPhoneMute Boolean
     * @param isCameraMute Boolean
     * @return PictureInPictureParams.Builder
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildPictureInPictureParams(isMicroPhoneMute: Boolean, isCameraMute: Boolean): PictureInPictureParams.Builder {
        logD { "$logTag, buildPictureInPictureParams: isMicroPhoneMute = $isMicroPhoneMute, isCameraMute = $isCameraMute" }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PictureInPictureParams.Builder()
                // Set action items for the picture-in-picture mode. These are the only custom controls
                // available during the picture-in-picture mode.
                .setActions(createPipActions(isMicroPhoneMute, isCameraMute))
                // Set the aspect ratio of the picture-in-picture mode.
                .setAspectRatio(pipRational)
                // if TRUE, Turn the screen into the picture-in-picture mode if it's hidden by the "Home" button.
                .setAutoEnterEnabled(false)
                // Disables the seamless resize. The seamless resize works great for videos where the
                // content can be arbitrarily scaled, but you can disable this for non-video content so
                // that the picture-in-picture mode is resized with a cross fade animation.
                .setSeamlessResizeEnabled(false)
        } else {
            PictureInPictureParams.Builder()
                // Set action items for the picture-in-picture mode. These are the only custom controls
                // available during the picture-in-picture mode.
                .setActions(createPipActions(isMicroPhoneMute, isCameraMute))
                // Set the aspect ratio of the picture-in-picture mode.
                .setAspectRatio(pipRational)
        }
    }

    /**
     * Create/Update Actions based on Microphone and Camera state
     * @param isMicroPhoneMute Boolean
     * @param isCameraMute Boolean
     * @return List<RemoteAction>
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPipActions(
        isMicroPhoneMute: Boolean,
        isCameraMute: Boolean
    ): List<RemoteAction> = listOf(
        createRemoteAction(PipAction.EndCall),
        if (isMicroPhoneMute) {
            createRemoteAction(PipAction.MicrophoneMute)
        } else {
            createRemoteAction(PipAction.MicrophoneUnMute)
        },
        if (isCameraMute) {
            createRemoteAction(PipAction.CameraMute)
        } else {
            createRemoteAction(PipAction.CameraUnMute)
        }
    )

    /**
     * Creates a [RemoteAction]. It is used as an action icon on the overlay of the
     * picture-in-picture mode.
     * @param pipAction PipAction
     * @return RemoteAction
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRemoteAction(pipAction: PipAction): RemoteAction{
        logD { "$logTag, createRemoteAction, pipAction: $pipAction" }
        return RemoteAction(
            Icon.createWithResource(context, pipAction.iconResId),
            context.getString(pipAction.titleResId),
            context.getString(pipAction.titleResId),
            PendingIntent.getBroadcast(
                context,
                pipAction.controlType,
                Intent(ACTION_PIP_CONTROL)
                    .putExtra(EXTRA_CONTROL_TYPE, pipAction.controlType),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}