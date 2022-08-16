package com.vidyo.vidyoconnector.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.analytics.AnalyticsManager
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceCategory
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceSwitch
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceTextField
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun GoogleAnalyticsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            TrackingEnabled()
            TrackingId()
            EventActionsPreferences()
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.preference_googleAnalytics)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun TrackingEnabled() {
    val preference = LocalConnectorManager.current.analytics.googleEnabled
    val value = preference.collectAsState().value

    PreferenceSwitch(
        name = stringResource(R.string.analytics_enabled),
        value = value,
        onChanged = { preference.value = it },
    )
}

@Composable
private fun TrackingId() {
    val preference = LocalConnectorManager.current.analytics.googleTrackingId
    val value = preference.collectAsState().value
    val empty = stringResource(R.string.analytics_empty)

    PreferenceTextField(
        name = stringResource(R.string.analytics_tracking_id),
        value = value,
        onDisplay = { it.ifEmpty { empty } },
        onChanged = { preference.value = it },
    )
}

@Composable
private fun EventActionsPreferences() {
    val analytics = LocalConnectorManager.current.analytics

    for ((category, events) in analytics.events) {
        Spacer(modifier = Modifier.height(50.dp))
        PreferenceCategory(name = category.textId)

        for (event in events) {
            EventActionPreference(event)
        }
    }
}

@Composable
private fun EventActionPreference(event: AnalyticsManager.EventActionInfo) {
    PreferenceSwitch(
        name = stringResource(event.action.textId),
        value = event.preference.collectAsState().value,
        onChanged = { event.preference.value = it },
    )
}
