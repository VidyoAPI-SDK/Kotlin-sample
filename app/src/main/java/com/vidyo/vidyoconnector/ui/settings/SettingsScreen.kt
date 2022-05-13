package com.vidyo.vidyoconnector.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.*

@Composable
@ComposableRoute
fun SettingsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            val navController = LocalNavController.current

            SettingsItem(
                icon = R.drawable.ic_settings_general,
                text = R.string.settings_general,
                onClick = { navController.navigateToGeneralSettingsScreen() },
            )
            SettingsItem(
                icon = R.drawable.ic_settings_audio,
                text = R.string.settings_audio,
                onClick = { navController.navigateToAudioSettingsScreen() },
            )
            SettingsItem(
                icon = R.drawable.ic_settings_video,
                text = R.string.settings_video,
                onClick = { navController.navigateToVideoSettingsScreen() },
            )
//            SettingsItem(
//                icon = R.drawable.ic_settings_account,
//                text = R.string.settings_account,
//                onClick = {},
//            )
            SettingsItem(
                icon = R.drawable.ic_settings_logs,
                text = R.string.settings_logs,
                onClick = { navController.navigateToLogsSettingsScreen() },
            )
            SettingsItem(
                icon = R.drawable.ic_settings_info,
                text = R.string.settings_about,
                onClick = { navController.navigateToAboutSettingsScreen() },
            )
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.settings_title)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes text: Int,
    onClick: () -> Unit,
) {
    val string = stringResource(text)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = string,
            colorFilter = ColorFilter.tint(
                MaterialTheme.colors.onBackground,
                blendMode = BlendMode.SrcIn
            ),
            modifier = Modifier
                .size(32.dp)
                .aspectRatio(ratio = 1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = string,
            fontSize = 24.sp,
            modifier = Modifier.weight(weight = 1f),
        )
    }
}
