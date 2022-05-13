package com.vidyo.vidyoconnector.bl.connector.media

import android.app.Activity
import android.content.Intent
import android.os.FileObserver
import androidx.core.content.FileProvider
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.Zip
import com.vidyo.vidyoconnector.utils.logD
import com.vidyo.vidyoconnector.utils.logE
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import java.io.File
import kotlin.time.Duration.Companion.seconds

class RecordingsManager(private val scope: ConnectorScope) {
    companion object : Loggable.Tag("RecordingsManager") {
        val recordFolder = File(ConnectorManager.rootFolder, "audio-recordings")
        val recordZipFile = File(ConnectorManager.rootFolder, "audio-recordings.zip")
    }

    private var shareRecordingsJob: Job? = null

    val totalSize = trackRecordings().shareIn(scope, SharingStarted.WhileSubscribed())

    fun shareRecordings(activity: Activity) {
        if (shareRecordingsJob?.isActive == true) {
            return
        }
        shareRecordingsJob = scope.launch {
            logD { "shareRecordings: compressing" }
            try {
                Zip.compress(recordFolder, recordZipFile)
            } catch (e: Exception) {
                return@launch logE(e) { "shareRecordings: failed" }
            }

            logD { "shareRecordings: sharing" }
            val stream = FileProvider.getUriForFile(activity, "com.vidyo.connector.file_provider", recordZipFile)
            val intent = Intent(Intent.ACTION_SEND)
                .setType("application/zip")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_SUBJECT, "Audio recordings")
                .putExtra(Intent.EXTRA_TEXT, "Audio recordings from the Connector app")
                .putExtra(Intent.EXTRA_STREAM, stream)

            activity.startActivity(intent)
        }
    }

    fun clearRecordings() {
        scope.launch(Dispatchers.Main) {
            logD { "clearRecordings: started" }
            try {
                for (file in listRecordingFiles()) {
                    logD { "clearRecordings: file = $file" }
                    file.delete()
                }
                logD { "clearRecordings: finished" }
            } catch (e: Exception) {
                logE(e) { "clearRecordings: failed" }
            }
        }
    }

    private fun trackRecordings() = flow {
        val channel = Channel<Unit>(Channel.CONFLATED)
        val mask = FileObserver.CREATE or FileObserver.DELETE or FileObserver.CLOSE_WRITE

        @Suppress("DEPRECATION")
        val observer = object : FileObserver(recordFolder.absolutePath, mask) {
            override fun onEvent(event: Int, path: String?) {
                channel.trySend(Unit)
            }
        }

        recordFolder.mkdirs()
        try {
            observer.startWatching()
            while (true) {
                var totalSize = 0L
                withContext(Dispatchers.Default) {
                    for (file in listRecordingFiles()) {
                        runCatching<Unit> { totalSize += file.length() }
                    }
                }

                logD { "trackRecordings: size = $totalSize" }
                emit(totalSize)

                delay(1.seconds)
                channel.receive()
            }
        } finally {
            observer.stopWatching()
        }
    }

    private fun listRecordingFiles() = sequence {
        if (recordZipFile.exists()) {
            yield(recordZipFile)
        }
        for (file in recordFolder.listFiles() ?: emptyArray()) {
            yield(file)
        }
    }
}
