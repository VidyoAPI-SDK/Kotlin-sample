package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.MutedState
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun CameraPrivacyIcon(modifier: Modifier) {
    val media = LocalConnectorManager.current.media.localCamera
    val state = media.muted.collectAsState()

    val image = when (state.value.muted) {
        true -> R.drawable.ic_camera_off
        else -> R.drawable.ic_camera_on
    }

    val tint = when (state.value) {
        MutedState.None -> null
        MutedState.Muted -> ColorFilter.tint(Color.Red)
        MutedState.ForceMuted -> ColorFilter.tint(Color.Gray)
    }

    IconButton(
        enabled = state.value != MutedState.ForceMuted,
        onClick = {
            media.requestMutedState(!state.value.muted)
        },
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = "camera muted ${state.value}",
            colorFilter = tint,
        )
    }
}
