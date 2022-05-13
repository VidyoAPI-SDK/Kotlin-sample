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
import com.vidyo.vidyoconnector.bl.connector.preferences.values.AudioCodec
import com.vidyo.vidyoconnector.ui.settings.preferences.Preference
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceCategory
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceList
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceSwitch
import com.vidyo.vidyoconnector.ui.utils.LocalActivity
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import com.vidyo.vidyoconnector.utils.FileSize
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun AudioSettingsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            PreferenceCategory(name = R.string.preference_device_selection_title)
            MicrophonePreference()
            SpeakerPreference()

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.settings_general)
            AudioCodecPreference()
            AudioPacketIntervalPreference()

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.preference_forward_error_correction_title)
            AudioPacketLossPercentagePreference()
            AudioBitrateMultiplierPreference()

            Spacer(modifier = Modifier.height(50.dp))

            PreferenceCategory(name = R.string.preference_audio_recording)
            RecordLocalMicrophonePreference()
            RecordLocalSpeakerPreference()
            ShareAudioRecords()
            ClearAudioRecordsCache()
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.settings_audio)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun MicrophonePreference() {
    val manager = LocalConnectorManager.current.media.localMicrophone
    val all = manager.all.collectAsState()
    val selected = manager.selected.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_microphone_title),
        value = selected.value,
        values = all.value,
        onDisplay = { it.name },
        onSelected = { manager.selectDevice(it) },
    )
}

@Composable
private fun SpeakerPreference() {
    val manager = LocalConnectorManager.current.media.localSpeaker
    val all = manager.all.collectAsState()
    val selected = manager.selected.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_speaker_title),
        value = selected.value,
        values = all.value,
        onDisplay = { it.name },
        onSelected = { manager.selectDevice(it) },
    )
}

@Composable
private fun AudioCodecPreference() {
    val preference = LocalConnectorManager.current.preferences.audioCodec
    val state = preference.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_audio_code_preference_title),
        value = state.value,
        values = AudioCodec.values().toList(),
        onDisplay = { stringResource(it.textId) },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun AudioPacketIntervalPreference() {
    val preference = LocalConnectorManager.current.preferences.audioPacketInterval
    val state = preference.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_audio_packet_interval_title),
        value = state.value,
        values = listOf(20, 40),
        onDisplay = { "$it ms" },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun AudioPacketLossPercentagePreference() {
    val preference = LocalConnectorManager.current.preferences.audioPacketLossPercentage
    val state = preference.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_packet_lost_title),
        value = state.value,
        values = listOf(0, 10, 20, 30),
        onDisplay = { "$it %" },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun AudioBitrateMultiplierPreference() {
    val preference = LocalConnectorManager.current.preferences.audioBitrateMultiplier
    val state = preference.collectAsState()

    PreferenceList(
        name = stringResource(R.string.preference_bit_rate_multiplier_title),
        value = state.value,
        values = listOf(0, 1, 2),
        onDisplay = { it.toString() },
        onSelected = { preference.value = it },
    )
}

@Composable
private fun RecordLocalSpeakerPreference() {
    val preference = LocalConnectorManager.current.media.localSpeaker.record
    val state = preference.collectAsState()

    PreferenceSwitch(
        name = stringResource(R.string.preference_audio_recordLocalSpeaker),
        value = state.value,
        onChanged = { preference.value = it },
    )
}

@Composable
private fun RecordLocalMicrophonePreference() {
    val preference = LocalConnectorManager.current.media.localMicrophone.record
    val state = preference.collectAsState()

    PreferenceSwitch(
        name = stringResource(R.string.preference_audio_recordLocalMicrophone),
        value = state.value,
        onChanged = { preference.value = it },
    )
}

@Composable
private fun ShareAudioRecords() {
    val activity = LocalActivity.current
    val media = LocalConnectorManager.current.media

    val recordings = media.recordings
    val recordingsSize = recordings.totalSize.collectAsState(0L)

    val speakerActive = media.localSpeaker.record.collectAsState()
    val microphoneActive = media.localMicrophone.record.collectAsState()
    val active = speakerActive.value || microphoneActive.value

    Preference(
        name = stringResource(R.string.preference_audio_shareRecordings),
        value = "",
        enabled = recordingsSize.value > 0 && !active,
        onClick = { recordings.shareRecordings(activity) },
    )
}

@Composable
private fun ClearAudioRecordsCache() {
    val media = LocalConnectorManager.current.media

    val recordings = media.recordings
    val recordingsSize = recordings.totalSize.collectAsState(0L)

    val speakerActive = media.localSpeaker.record.collectAsState()
    val microphoneActive = media.localMicrophone.record.collectAsState()
    val active = speakerActive.value || microphoneActive.value

    Preference(
        name = stringResource(R.string.preference_audio_clearRecordings),
        value = FileSize.toString(recordingsSize.value),
        enabled = recordingsSize.value > 0 && !active,
        onClick = { recordings.clearRecordings() },
    )
}
