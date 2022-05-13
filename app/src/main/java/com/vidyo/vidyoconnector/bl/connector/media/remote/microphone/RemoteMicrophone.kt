package com.vidyo.vidyoconnector.bl.connector.media.remote.microphone

import com.vidyo.VidyoClient.Device.RemoteMicrophone as VcRemoteMicrophone

data class RemoteMicrophone(
    val id: String,
    val name: String,
    val participantId: String,
) {
    companion object {
        fun from(other: VcRemoteMicrophone, participantId: String) = RemoteMicrophone(
            id = other.id.orEmpty(),
            name = other.name.orEmpty(),
            participantId = participantId,
        )
    }
}
