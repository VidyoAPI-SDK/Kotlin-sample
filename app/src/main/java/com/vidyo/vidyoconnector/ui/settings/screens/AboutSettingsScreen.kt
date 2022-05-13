package com.vidyo.vidyoconnector.ui.settings.screens

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun AboutSettingsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_vidyo_platform_logo),
                contentDescription = "logo",
                modifier = Modifier.height(100.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.settingsAbout_title),
                fontSize = 28.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                GradientLine(
                    modifier = Modifier
                        .size(width = 64.dp, height = 1.dp)
                        .rotate(180f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = LocalConnectorManager.current.version)
                Spacer(modifier = Modifier.width(8.dp))
                GradientLine(modifier = Modifier.size(width = 64.dp, height = 1.dp))
            }

            val html = stringResource(R.string.settingsAbout_message)
            val color = MaterialTheme.colors.onBackground
            Spacer(modifier = Modifier.height(32.dp))
            AndroidView(
                factory = { TextView(it) },
                update = {
                    it.setTextColor(color.toArgb())
                    it.movementMethod = LinkMovementMethod.getInstance()
                    it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(weight = 1f))
            Text(text = stringResource(R.string.settingsAbout_copyright))
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.settings_about)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun GradientLine(modifier: Modifier = Modifier) {
    val brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colors.onBackground,
            MaterialTheme.colors.onBackground.copy(alpha = 0f),
        )
    )
    Box(modifier = modifier.background(brush = brush))
}
