package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import dev.matrix.compose_routes.navigateToVirtualBackgroundScreen

@Composable
fun CameraEffectIcon(modifier: Modifier) {
    val navController = LocalNavController.current

    IconButton(
        onClick = { navController.navigateToVirtualBackgroundScreen() },
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_camera_effect),
            contentDescription = "camera effect",
        )
    }
}
