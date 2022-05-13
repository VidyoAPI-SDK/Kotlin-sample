package com.vidyo.vidyoconnector.bl.connector.media.local.speaker

import com.vidyo.vidyoconnector.bl.connector.media.RecordingsManager
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.logD
import java.io.File
import com.vidyo.VidyoClient.Device.LocalSpeaker as VcLocalSpeaker

data class LocalSpeaker(
    val id: String,
    val name: String,
    val handle: VcLocalSpeaker?,
) {
    companion object : Loggable.Tag("LocalSpeaker") {
        val Null = LocalSpeaker(id = "", name = "", handle = null)

        fun from(other: VcLocalSpeaker) = LocalSpeaker(
            id = other.id.orEmpty(),
            name = other.name.orEmpty(),
            handle = other,
        )
    }

    override fun toString(): String {
        return "LocalSpeaker(id='$id', name='$name')"
    }

    fun setDebugRecordingsPath(path: File?) {
        logD { "setDebugRecordingsPath: id = $id, path = $path" }

        val handle = handle ?: return
        when (path != null) {
            true -> handle.enableDebugRecordings(RecordingsManager.recordFolder.absolutePath)
            else -> handle.disableDebugRecordings()
        }
    }
}
