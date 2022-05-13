package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import dev.matrix.compose_routes.navigateToSettingsScreen

@Composable
fun SettingsIcon(modifier: Modifier) {
    val navController = LocalNavController.current

    IconButton(
        onClick = { navController.navigateToSettingsScreen() },
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = "settings",
        )
    }
}
