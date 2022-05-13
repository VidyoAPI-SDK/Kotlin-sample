package com.vidyo.vidyoconnector.bl.connector.participants

import androidx.annotation.StringRes
import com.vidyo.vidyoconnector.R
import com.vidyo.VidyoClient.Endpoint.Participant.ParticipantTrust as VcParticipantTrust

enum class ParticipantTrust(
    @StringRes val textId: Int,
    val jniValue: VcParticipantTrust,
) {
    Local(
        textId = R.string.ParticipantTrust_Local,
        jniValue = VcParticipantTrust.VIDYO_PARTICIPANTTRUST_Local,
    ),
    Federated(
        textId = R.string.ParticipantTrust_Federated,
        jniValue = VcParticipantTrust.VIDYO_PARTICIPANTTRUST_Federated,
    ),
    Anonymous(
        textId = R.string.ParticipantTrust_Anonymous,
        jniValue = VcParticipantTrust.VIDYO_PARTICIPANTTRUST_Anonymous,
    );

    companion object {
        fun fromJniValue(jniValue: VcParticipantTrust): ParticipantTrust {
            return values().find { it.jniValue == jniValue } ?: Local
        }
    }
}
