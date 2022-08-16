package com.vidyo.vidyoconnector.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceSwitch
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceTextField
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun InsightsAnalyticsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            TrackingEnabled()
            TrackingId()
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.preference_insightsAnalytics)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun TrackingEnabled() {
    val preference = LocalConnectorManager.current.analytics.insightEnabled
    val value = preference.collectAsState().value

    PreferenceSwitch(
        name = stringResource(R.string.analytics_enabled),
        value = value,
        onChanged = { preference.value = it },
    )
}

@Composable
private fun TrackingId() {
    val preference = LocalConnectorManager.current.analytics.insightAddress
    val info = preference.collectAsState().value
    val empty = stringResource(R.string.analytics_empty)

    PreferenceTextField(
        name = stringResource(R.string.analytics_ip_address),
        value = info,
        onDisplay = { it.ifEmpty { empty } },
        onChanged = { preference.value = it },
    )
}
