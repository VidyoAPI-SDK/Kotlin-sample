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
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun MicrophonePrivacyIcon(modifier: Modifier) {
    val media = LocalConnectorManager.current.media.localMicrophone
    val state = media.muted.collectAsState()

    val image = when (state.value) {
        true -> R.drawable.ic_microphone_off
        else -> R.drawable.ic_microphone_on
    }

    val tint = when (state.value) {
        true -> ColorFilter.tint(Color.Red)
        else -> null
    }

    IconButton(
        onClick = {
            media.requestMutedState(!state.value)
        },
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = "microphone muted ${state.value}",
            colorFilter = tint,
        )
    }
}
