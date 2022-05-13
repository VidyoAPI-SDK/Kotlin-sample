package com.vidyo.vidyoconnector.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.logs.LogLevel
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceList
import com.vidyo.vidyoconnector.ui.settings.preferences.PreferenceTextField
import com.vidyo.vidyoconnector.ui.utils.LocalActivity
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcTextButton
import dev.matrix.compose_routes.ComposableRoute
import kotlinx.coroutines.channels.Channel

@Composable
@ComposableRoute
fun LogsSettingsScreen() {
    Scaffold(topBar = { AppBar() }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val manager = LocalConnectorManager.current.logs
            val logs = rememberSaveable { mutableStateOf(emptyList<String>()) }
            val logsTrigger = remember { Channel<Unit>(Channel.CONFLATED) }

            LaunchedEffect("logs_reader") {
                while (true) {
                    logsTrigger.receive()
                    logs.value = manager.readLogsFileLines()
                }
            }

            LogLevelPreference()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f),
            ) {
                val items = logs.value
                items(items.size) {
                    LogRecord(items[it])
                }
            }

            VcTextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { logsTrigger.trySend(Unit) },
            ) {
                Text(text = stringResource(id = R.string.settingsLogs_displayLogs))
            }
        }
    }
}

@Composable
private fun AppBar() {
    val activity = LocalActivity.current
    val manager = LocalConnectorManager.current.logs

    TopAppBar(
        title = { Text(text = stringResource(R.string.settings_logs)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
        actions = {
            IconButton(onClick = { manager.shareLogs(activity) }) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Share),
                    contentDescription = "share logs",
                    tint = MaterialTheme.colors.onSurface,
                )
            }
        }
    )
}

@Composable
private fun LogLevelPreference() {
    val manager = LocalConnectorManager.current.logs
    val level = manager.level.collectAsState()
    val filter = manager.filter.collectAsState()

    Column {
        PreferenceList(
            name = stringResource(R.string.settingsLogs_logLevel),
            value = level.value,
            values = LogLevel.values().toList(),
            onDisplay = { stringResource(it.textId) },
            onSelected = { manager.level.value = it },
        )

        if (level.value == LogLevel.Advanced) {
            PreferenceTextField(
                name = stringResource(R.string.settingsLogs_logFilter),
                value = filter.value,
                onChanged = { manager.filter.value = it },
            )
        }
    }
}

@Composable
private fun LogRecord(message: String) {
    Text(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        text = message,
    )
}
