package com.vidyo.vidyoconnector.bl.connector.participants

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Endpoint.Participant.ParticipantClearanceType
import com.vidyo.vidyoconnector.R

enum class ParticipantClearance(
    @StringRes val textId: Int,
    val jniValue: ParticipantClearanceType,
) {
    None(
        textId = R.string.ParticipantClearance_None,
        jniValue = ParticipantClearanceType.VIDYO_PARTICIPANT_CLEARANCETYPE_None
    ),
    Member(
        textId = R.string.ParticipantClearance_Member,
        jniValue = ParticipantClearanceType.VIDYO_PARTICIPANT_CLEARANCETYPE_Member
    ),
    Owner(
        textId = R.string.ParticipantClearance_Owner,
        jniValue = ParticipantClearanceType.VIDYO_PARTICIPANT_CLEARANCETYPE_Owner
    ),
    Admin(
        textId = R.string.ParticipantClearance_Admin,
        jniValue = ParticipantClearanceType.VIDYO_PARTICIPANT_CLEARANCETYPE_Admin
    ),
    Moderator(
        textId = R.string.ParticipantClearance_Moderator,
        jniValue = ParticipantClearanceType.VIDYO_PARTICIPANT_CLEARANCETYPE_Moderator
    );

    companion object {
        fun fromJniValue(jniValue: ParticipantClearanceType): ParticipantClearance {
            return values().find { it.jniValue == jniValue } ?: None
        }
    }
}
