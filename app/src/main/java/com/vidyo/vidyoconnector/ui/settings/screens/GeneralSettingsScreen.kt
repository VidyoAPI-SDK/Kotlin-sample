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
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.bl.connector.analytics.AnalyticsType
import com.vidyo.vidyoconnector.bl.connector.preferences.values.CpuTradeOffProfile
import com.vidyo.vidyoconnector.ui.settings.preferences.Preference
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceCategory
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceList
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceSwitch
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute
import dev.matrix.compose_routes.navigateToAnalyticsScreen

@Composable
@ComposableRoute
fun GeneralSettingsScreen() {
    val settingsEnabled = !ConnectorManager.conference.conference.collectAsState().value.state.isActive

    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            PreferenceCategory(name = R.string.settings_general)
            CpuTradeOffProfilePreference(enabled = settingsEnabled)
            NetworkForSignallingPreference(enabled = settingsEnabled)
            NetworkForMediaPreference(enabled = settingsEnabled)
            NumberOfParticipantsPreference(enabled = settingsEnabled)
            SelfViewOptionsPreference(enabled = settingsEnabled)

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.preference_auto_reconnect_title)
            AutoReconnectPreference(enabled = settingsEnabled)
            AutoReconnectMaxAttemptsPreference(enabled = settingsEnabled)
            AutoReconnectAttemptBackOffPreference(enabled = settingsEnabled)

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.preference_analytics_title)
            EnableAnalyticsPreference(enabled = settingsEnabled)
            TypeOfAnalyticsPreference(enabled = settingsEnabled)
            OpenAnalyticsPreference(enabled = settingsEnabled)
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.settings_general)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun CpuTradeOffProfilePreference(enabled: Boolean) {
    val preference = LocalConnectorManager.current.preferences.cpuTradeOffProfile
    val state = preference.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_cpu_profile_title),
        value = state.value,
        values = CpuTradeOffProfile.values().toList(),
        enabled = enabled,
        onDisplay = { stringResource(it.textId) },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun NetworkForSignallingPreference(enabled: Boolean) {
    val manager = LocalConnectorManager.current.networks
    val value = manager.networkForSignaling.collectAsState()
    val values = manager.networks.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_network_for_signaling_title),
        value = value.value,
        values = values.value.toList(),
        enabled = enabled,
        onDisplay = { it.name },
        onSelected = { manager.selectNetworkForSignaling(it) },
    )
}

@Composable
private fun NetworkForMediaPreference(enabled: Boolean) {
    val manager = LocalConnectorManager.current.networks
    val value = manager.networkForMedia.collectAsState()
    val values = manager.networks.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_network_for_media_title),
        value = value.value,
        values = values.value.toList(),
        enabled = enabled,
        onDisplay = { it.name },
        onSelected = { manager.selectNetworkForMedia(it) },
    )
}

@Composable
private fun NumberOfParticipantsPreference(enabled: Boolean) {
    val preference = LocalConnectorManager.current.preferences.numberOfParticipants
    val state = preference.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_number_of_participants_title),
        value = state.value,
        values = (0..9).toList(),
        enabled = enabled,
        onDisplay = { it.toString() },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun SelfViewOptionsPreference(enabled: Boolean) {
    Preference(
        name = stringResource(R.string.preference_self_view_options_title),
        value = "Bottom right",
        enabled = enabled,
        onClick = {},
    )
}

@Composable
private fun AutoReconnectPreference(enabled: Boolean) {
    val preference = LocalConnectorManager.current.preferences.autoReconnect
    val state = preference.collectAsState()

    PreferenceSwitch(
        name = stringResource(R.string.preference_auto_reconnect_title),
        value = state.value,
        enabled = enabled,
        onChanged = { preference.value = it },
    )
}

@Composable
private fun AutoReconnectMaxAttemptsPreference(enabled: Boolean) {
    val preferences = LocalConnectorManager.current.preferences
    val preference = preferences.autoReconnectMaxAttempts
    val state = preference.collectAsState()
    val autoReconnectEnabled = preferences.autoReconnect.collectAsState().value

    PreferenceList(
        name = stringResource(R.string.preference_max_reconnect_attempts_title),
        value = state.value,
        values = (1..4).toList(),
        enabled = enabled && autoReconnectEnabled,
        onDisplay = { it.toString() },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun AutoReconnectAttemptBackOffPreference(enabled: Boolean) {
    val preferences = LocalConnectorManager.current.preferences
    val preference = preferences.autoReconnectAttemptBackOff
    val state = preference.collectAsState()
    val autoReconnectEnabled = preferences.autoReconnect.collectAsState().value

    PreferenceList(
        name = stringResource(R.string.preference_reconnect_back_off_title),
        value = state.value,
        values = (2..20).toList(),
        enabled = enabled && autoReconnectEnabled,
        onDisplay = { it.toString() },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun EnableAnalyticsPreference(enabled: Boolean) {
    val manager = LocalConnectorManager.current.analytics
    val value = manager.enabled.collectAsState().value

    PreferenceSwitch(
        name = stringResource(R.string.preference_enable_analytics_title),
        value = value,
        enabled = enabled,
        onChanged = { manager.enabled.value = it },
    )
}

@Composable
private fun TypeOfAnalyticsPreference(enabled: Boolean) {
    val manager = LocalConnectorManager.current.analytics
    val value = manager.type.collectAsState().value
    val analyticsEnabled = manager.enabled.collectAsState().value

    PreferenceList(
        name = stringResource(R.string.preference_type_of_analytics_title),
        value = value,
        values = AnalyticsType.values().filter { it != AnalyticsType.None },
        enabled = enabled && analyticsEnabled,
        onDisplay = { stringResource(it.textId) },
        onSelected = { manager.type.value = it },
    )
}

@Composable
private fun OpenAnalyticsPreference(enabled: Boolean) {
    val manager = LocalConnectorManager.current.analytics
    val type = manager.type.collectAsState().value
    val analyticsEnabled = manager.enabled.collectAsState().value

    val navController = LocalNavController.current

    Preference(
        name = stringResource(R.string.preference_analyticsType, stringResource(type.textId)),
        value = "",
        enabled = enabled && analyticsEnabled,
        onClick = {
            navController.navigateToAnalyticsScreen(type)
        },
    )
}
