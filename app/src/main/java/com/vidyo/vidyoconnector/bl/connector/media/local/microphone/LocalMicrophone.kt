package com.vidyo.vidyoconnector.bl.connector.media.local.microphone

import com.vidyo.vidyoconnector.bl.connector.media.RecordingsManager
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.logD
import java.io.File
import com.vidyo.VidyoClient.Device.LocalMicrophone as VcLocalMicrophone

data class LocalMicrophone(
    val id: String,
    val name: String,
    val handle: VcLocalMicrophone?,
) {
    companion object : Loggable.Tag("LocalMicrophone") {
        val Null = LocalMicrophone(id = "", name = "", handle = null)

        fun from(other: VcLocalMicrophone) = LocalMicrophone(
            id = other.id.orEmpty(),
            name = other.name.orEmpty(),
            handle = other,
        )
    }

    override fun toString(): String {
        return "LocalMicrophone(id='$id', name='$name')"
    }

    fun setDebugRecordingsPath(path: File?) {
        logD { "setDebugRecordingsPath: id = $id, path = $path" }

        val handle = handle ?: return
        when (path != null) {
            true -> handle.enableDebugRecording(RecordingsManager.recordFolder.absolutePath)
            else -> handle.disableDebugRecording()
        }
    }
}
