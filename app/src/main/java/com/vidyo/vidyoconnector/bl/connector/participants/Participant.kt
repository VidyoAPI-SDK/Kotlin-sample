package com.vidyo.vidyoconnector.bl.connector.participants

import com.vidyo.VidyoClient.Endpoint.Participant as VcParticipant

data class Participant(
    val id: String,
    val name: String,
    val isLocal: Boolean,
    val trust: ParticipantTrust,
    val clearance: ParticipantClearance,
    val handle: VcParticipant?,
) {
    companion object {
        fun from(other: VcParticipant) = Participant(
            id = other.id.orEmpty(),
            name = other.name.orEmpty(),
            isLocal = other.isLocal,
            trust = ParticipantTrust.fromJniValue(other.trust),
            clearance = ParticipantClearance.fromJniValue(other.clearanceType),
            handle = other,
        )
    }

    val initials = name.split(" ").take(2).joinToString(separator = "") {
        val ch = it.firstOrNull()
        when (ch == null) {
            true -> ""
            else -> ch.uppercaseChar().toString()
        }
    }
}
