package com.vidyo.vidyoconnector.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.local.camera.distinctByFrameIntervals
import com.vidyo.vidyoconnector.bl.connector.media.local.camera.distinctBySizes
import com.vidyo.vidyoconnector.bl.connector.preferences.values.Bitrate
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceCategory
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceList
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceSwitch
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceTextField
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun VideoSettingsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            PreferenceCategory(name = R.string.preference_device_selection_title)
            CameraPreference()

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.settings_general)
            ResolutionPreference()
            FrameRatePreference()
            DisableVideoOnLowBandwidthPreference()
            DisableVideoOnLowBandwidthResponseTimePreference()
            DisableVideoOnLowBandwidthSampleTimePreference()
            DisableVideoOnLowBandwidthThresholdPreference()
            DisableVideoOnLowBandwidthAudioStreamsPreference()

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.preference_max_allowed_bandwidth_title)
            MaxSendBitRatePreference()
            MaxReceiveBitRatePreference()
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.settings_video)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun CameraPreference() {
    val manager = LocalConnectorManager.current.media.localCamera
    val all = manager.all.collectAsState()
    val selected = manager.selected.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_camera_title),
        value = selected.value,
        values = all.value,
        onDisplay = { it?.name ?: "" },
        onSelected = { it?.also { manager.selectDevice(it) } },
    )
}

@Composable
private fun ResolutionPreference() {
    val manager = LocalConnectorManager.current

    val camera = manager.media.localCamera.selected.collectAsState().value
    val preference = manager.preferences.localCameraConstraints
    val constraints = preference.collectAsState().value

    var all = camera?.constraints.orEmpty()
    all = when (constraints == null) {
        true -> all.distinctBySizes()
        else -> all.filter { it.frameInterval == constraints.frameInterval }
    }

    PreferenceList(
        name = stringResource(R.string.preference_resolution_title),
        value = constraints,
        values = all,
        onDisplay = { it?.let { "${it.width}x${it.height}" } ?: "" },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun FrameRatePreference() {
    val manager = LocalConnectorManager.current

    val camera = manager.media.localCamera.selected.collectAsState().value
    val preference = manager.preferences.localCameraConstraints
    val constraints = preference.collectAsState().value

    var all = camera?.constraints.orEmpty()
    all = when (constraints == null) {
        true -> all.distinctByFrameIntervals()
        else -> all.filter { it.width == constraints.width && it.height == constraints.height }
    }

    PreferenceList(
        name = stringResource(R.string.preference_frame_rate_title),
        value = constraints,
        values = all,
        onDisplay = { it?.fps?.toString() ?: "" },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun DisableVideoOnLowBandwidthPreference() {
    val preference = LocalConnectorManager.current.preferences.disableVideoOnLowBandwidth
    val state = preference.collectAsState()

    PreferenceSwitch(
        name = stringResource(R.string.preference_disable_video_on_poor_connection_title),
        value = state.value,
        onChanged = { preference.value = it },
    )
}

@Composable
private fun DisableVideoOnLowBandwidthResponseTimePreference() {
    val preferences = LocalConnectorManager.current.preferences
    val preference = preferences.disableVideoOnLowBandwidthResponseTime
    val state = preference.collectAsState()
    val enabled = preferences.disableVideoOnLowBandwidth.collectAsState()

    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
    )

    PreferenceTextField(
        name = stringResource(R.string.preference_response_time_title),
        value = state.value.toString(),
        enabled = enabled.value,
        keyboardOptions = keyboardOptions,
        onDisplay = { "$it ms" },
        onChanged = { it.toIntOrNull()?.also { int -> preference.value = int } },
    )
}

@Composable
private fun DisableVideoOnLowBandwidthSampleTimePreference() {
    val preferences = LocalConnectorManager.current.preferences
    val preference = preferences.disableVideoOnLowBandwidthSampleTime
    val state = preference.collectAsState()
    val enabled = preferences.disableVideoOnLowBandwidth.collectAsState()

    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
    )

    PreferenceTextField(
        name = stringResource(R.string.preference_sample_time_title),
        value = state.value.toString(),
        enabled = enabled.value,
        keyboardOptions = keyboardOptions,
        onDisplay = { "$it ms" },
        onChanged = { it.toIntOrNull()?.also { int -> preference.value = int } },
    )
}

@Composable
private fun DisableVideoOnLowBandwidthThresholdPreference() {
    val preferences = LocalConnectorManager.current.preferences
    val preference = preferences.disableVideoOnLowBandwidthThreshold
    val state = preference.collectAsState()
    val enabled = preferences.disableVideoOnLowBandwidth.collectAsState()

    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
    )

    PreferenceTextField(
        name = stringResource(R.string.preference_low_bandwidth_threshold_title),
        value = state.value.toString(),
        enabled = enabled.value,
        keyboardOptions = keyboardOptions,
        onDisplay = { "$it kBps" },
        onChanged = { it.toIntOrNull()?.also { int -> preference.value = int } },
    )
}

@Composable
private fun DisableVideoOnLowBandwidthAudioStreamsPreference() {
    val preferences = LocalConnectorManager.current.preferences
    val preference = preferences.disableVideoOnLowBandwidthAudioStreams
    val state = preference.collectAsState()
    val enabled = preferences.disableVideoOnLowBandwidth.collectAsState()

    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
    )

    PreferenceTextField(
        name = stringResource(R.string.preference_audio_streams_title),
        value = state.value.toString(),
        enabled = enabled.value,
        keyboardOptions = keyboardOptions,
        onChanged = { it.toIntOrNull()?.also { int -> preference.value = int } },
    )
}

@Composable
private fun MaxSendBitRatePreference() {
    val preference = LocalConnectorManager.current.preferences.maxSendBitRate
    val state = preference.collectAsState()

    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
    )

    PreferenceTextField(
        name = stringResource(R.string.preference_max_send_bit_rate_title),
        value = state.value.toString(),
        keyboardOptions = keyboardOptions,
        onDisplay = { Bitrate.toString(Bitrate.parse(it)) },
        onChanged = { preference.value = Bitrate.parse(it) },
    )
}

@Composable
private fun MaxReceiveBitRatePreference() {
    val preference = LocalConnectorManager.current.preferences.maxReceiveBitRate
    val state = preference.collectAsState()

    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
    )

    PreferenceTextField(
        name = stringResource(R.string.preference_max_receive_bit_rate_title),
        value = state.value.toString(),
        keyboardOptions = keyboardOptions,
        onDisplay = { Bitrate.toString(Bitrate.parse(it)) },
        onChanged = { preference.value = Bitrate.parse(it) },
    )
}
