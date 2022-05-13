package com.vidyo.vidyoconnector.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.analytics.AnalyticsEventCategory
import com.vidyo.vidyoconnector.bl.connector.analytics.AnalyticsManager
import com.vidyo.vidyoconnector.bl.connector.analytics.AnalyticsType
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceCategory
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceSwitch
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceTextField
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun AnalyticsScreen(type: AnalyticsType) {
    Scaffold(topBar = { AppBar(type) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            when (type) {
                AnalyticsType.Google -> GooglePreferences()
                AnalyticsType.VidyoInsight -> VidyoInsightPreferences()
                AnalyticsType.None -> Unit
            }

            EventActionsPreferences()
        }
    }
}

@Composable
private fun AppBar(type: AnalyticsType) {
    val typeName = stringResource(type.textId)

    TopAppBar(
        title = { Text(text = stringResource(R.string.preference_analyticsType, typeName)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun GooglePreferences() {
    val preference = LocalConnectorManager.current.analytics.google
    val info = preference.collectAsState().value
    val empty = stringResource(R.string.analytics_empty)

    PreferenceTextField(
        name = stringResource(R.string.analytics_web_property),
        value = info.trackingId,
        onDisplay = { it.ifEmpty { empty } },
        onChanged = { preference.value = info.copy(trackingId = it) },
    )
}

@Composable
private fun VidyoInsightPreferences() {
    val preference = LocalConnectorManager.current.analytics.vidyoInsight
    val info = preference.collectAsState().value
    val empty = stringResource(R.string.analytics_empty)

    PreferenceTextField(
        name = stringResource(R.string.analytics_ip_address),
        value = info.serverUrl,
        onDisplay = { it.ifEmpty { empty } },
        onChanged = { preference.value = info.copy(serverUrl = it) },
    )
}

@Composable
private fun EventActionsPreferences() {
    val analytics = LocalConnectorManager.current.analytics

    var lastCategory = AnalyticsEventCategory.None
    for (event in analytics.events) {
        if (lastCategory != event.action.category) {
            lastCategory = event.action.category

            Spacer(modifier = Modifier.height(50.dp))
            PreferenceCategory(name = lastCategory.textId)
        }

        EventActionPreference(event)
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
