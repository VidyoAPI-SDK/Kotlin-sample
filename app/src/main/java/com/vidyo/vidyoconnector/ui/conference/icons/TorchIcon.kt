package com.vidyo.vidyoconnector.ui.conference.icons


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.vidyo.VidyoClient.Device.LocalCamera.LocalCameraTorchMode
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.logD
import com.vidyo.vidyoconnector.utils.logE

class Log : Loggable {
    override val logTag: String = "LocalCamera"

    // Other members and methods...
}

val Logger = Log()

@Composable
fun TorchIcon(modifier: Modifier) {
    val media = LocalConnectorManager.current.media.localCamera
    val selectedCamera = media.selected.collectAsState()
    //val cameraState = media.muted.collectAsState()
    val torchAvailable = remember { mutableStateOf(false) }
    var nextTorchMode by remember { mutableStateOf(LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_None) }
    LaunchedEffect(selectedCamera) {
        when {
            selectedCamera.value?.handle?.hasTorch() == true -> torchAvailable.value = true
        }
        if (torchAvailable.value && selectedCamera.value != null) {
            nextTorchMode = selectedCamera.value?.handle?.getTorchMode()!!
            Logger.logD { "Cam - ${selectedCamera.value?.name}, initial torch mode - $nextTorchMode" }
        }
    }

    IconButton(
        onClick = {
            try {
                //if (torchAvailable.value == true && cameraState.value.muted == false) {
                if (torchAvailable.value) {
                    val torchMode = selectedCamera.value?.handle?.getTorchMode()
                    if (null == torchMode ||
                        LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_None == torchMode) {
                        // do nothing. // may be a log
                        Logger.logE { "Cam - ${selectedCamera.value?.name}, could not get torch mode" }
                    }
                    else {
                        nextTorchMode = when (torchMode) {
                            LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_Off ->
                                LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_On
                            LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_On ->
                                LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_Off
                            else ->
                                LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_None
                        }

                        Logger.logD { "Cam - ${selectedCamera.value?.name}, curr torch mode - $torchMode, next mode $nextTorchMode" }

                        if (selectedCamera.value?.handle?.isTorchModeSupported(nextTorchMode) == false) {
                            Logger.logE { "Cam - ${selectedCamera.value?.name}, torch mode $nextTorchMode is not supported" }
                            nextTorchMode = torchMode
                        }
                        else {
                            val ret = selectedCamera.value?.handle?.setTorchMode(nextTorchMode)
                            if (false == ret) {
                                Logger.logE { "Cam - ${selectedCamera.value?.name}, failed to set torch mode $nextTorchMode" }
                                nextTorchMode = torchMode
                            }
                            else {
                                // success
                                Logger.logD { "Cam - ${selectedCamera.value?.name}, torch mode $nextTorchMode is set" }
                            }
                        }
                    }
                }
            }
            catch(exception:Exception){
                Logger.logE (exception) { "An exception occurred: ${exception.message}" }
            }
        },
        modifier = modifier,
        enabled = torchAvailable.value
    ) {
        Image(
            painter = painterResource(R.drawable.torch),
            contentDescription = "Torch",
            colorFilter = if (torchAvailable.value) {
                when (nextTorchMode) {
                    LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_Off -> ColorFilter.tint(Color.Red)
                    LocalCameraTorchMode.VIDYO_LOCALCAMERA_TORCHMODE_On -> ColorFilter.tint(Color.White)
                    else -> ColorFilter.tint(Color.DarkGray)
                }
            } else {
                ColorFilter.tint(Color.DarkGray)
            }
        )
        Logger.logD { "Cam - ${selectedCamera.value?.name}, update icon - torch mode $nextTorchMode" }
    }
}
