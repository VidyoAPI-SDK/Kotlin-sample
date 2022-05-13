package com.vidyo.vidyoconnector.bl.connector.logs

import android.app.Activity
import android.content.Intent
import androidx.core.content.FileProvider
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesManager
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.Zip
import com.vidyo.vidyoconnector.utils.logD
import com.vidyo.vidyoconnector.utils.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LogsManager(private val scope: ConnectorScope, preferences: PreferencesManager) {
    companion object : Loggable.Tag("LogsManager") {
        private val logsFolder = File(ConnectorManager.rootFolder, "logs")
        private val logsZipFile = File(ConnectorManager.rootFolder, "logs.zip")
        val logsFile = File(logsFolder, "VidyoConnector.log")
    }

    private var shareLogsJob: Job? = null
    private val loggerTypes = arrayOf(
        Connector.ConnectorLoggerType.VIDYO_CONNECTORLOGGERTYPE_FILE,
        Connector.ConnectorLoggerType.VIDYO_CONNECTORLOGGERTYPE_CONSOLE,
    )

    val level = preferences.createPreferencesProperty(
        key = "log_level",
        read = {
            LogLevel.fromOrdinal(getInt(it, -1)) {
                LogLevel.fromJniValue(scope.connector.getLogLevel(loggerTypes.first())) {
                    LogLevel.Production
                }
            }
        },
        write = { key, value -> putInt(key, value.ordinal) },
    )

    val filter = preferences.createPreferencesProperty(
        key = "log_filter",
        read = { getString(it, null) ?: "debug@VidyoConnector debug@VidyoClient" },
        write = { key, value -> putString(key, value) },
    )

    init {
        combine(level, filter) { level, filter ->
            for (loggerType in loggerTypes) {
                when (level == LogLevel.Advanced) {
                    true -> scope.connector.setAdvancedLogOptions(loggerType, filter)
                    else -> scope.connector.setLogLevel(loggerType, level.jniValue)
                }
            }
        }.launchIn(scope)
    }

    suspend fun readLogsFileLines(): List<String> {
        return withContext(Dispatchers.IO) {
            logsFile.readLines()
        }
    }

    fun shareLogs(activity: Activity) {
        if (shareLogsJob?.isActive == true) {
            return
        }
        shareLogsJob = scope.launch {
            logD { "shareLogs: compressing" }
            try {
                Zip.compress(logsFolder, logsZipFile)
            } catch (e: Exception) {
                return@launch logE(e) { "shareRecordings: failed" }
            }

            logD { "shareLogs: sharing" }
            val stream = FileProvider.getUriForFile(activity, "com.vidyo.connector.file_provider", logsZipFile)
            val intent = Intent(Intent.ACTION_SEND)
                .setType("application/zip")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_SUBJECT, "Connector logs")
                .putExtra(Intent.EXTRA_TEXT, "Logs from the Connector app")
                .putExtra(Intent.EXTRA_STREAM, stream)

            activity.startActivity(intent)
        }
    }
}
