package com.vidyo.vidyoconnector.bl.connector.media.remote.share

import com.vidyo.vidyoconnector.bl.connector.participants.Participant
import com.vidyo.VidyoClient.Device.RemoteWindowShare as VcRemoteWindowShare

data class RemoteScreenShare(
    val id: String,
    val name: String,
    val participant: Participant,
) {
    companion object {
        fun from(other: VcRemoteWindowShare, participant: Participant) = RemoteScreenShare(
            id = other.id.orEmpty(),
            name = other.name.orEmpty(),
            participant = participant,
        )
    }
}
