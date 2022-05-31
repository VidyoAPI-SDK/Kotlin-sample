package com.vidyo.vidyoconnector.bl.connector.conference

data class Conference(
    val state: ConferenceState,
    val joinInfo: ConferenceJoinInfo? = null,
) {
    companion object {
        val Null = Conference(state = ConferenceState.Idle)
    }
}
