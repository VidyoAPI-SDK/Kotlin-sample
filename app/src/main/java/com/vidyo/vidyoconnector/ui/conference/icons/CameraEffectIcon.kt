package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.virtual_background.PreviewVirtualBackgroundDialog

@Composable
fun CameraEffectIcon(modifier: Modifier) {
    val dialog = rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { dialog.value = true },
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_camera_effect),
            contentDescription = "camera effect",
        )
    }

    if (dialog.value) {
        PreviewVirtualBackgroundDialog { dialog.value = false }
    }
}
