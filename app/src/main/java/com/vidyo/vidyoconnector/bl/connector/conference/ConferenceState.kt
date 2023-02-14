package com.vidyo.vidyoconnector.bl.connector.conference

import android.content.Context
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.R
import kotlin.time.Duration
import kotlin.time.TimeMark

sealed class ConferenceState(val isActive: Boolean) {

    companion object {
        private val FailReasonPattern = Regex("^VIDYO_CONNECTORFAILREASON_(.*)$")
    }

    object Idle : ConferenceState(isActive = false)

    object Joining : ConferenceState(isActive = true)
    object Joined : ConferenceState(isActive = true)

    class Reconnecting(
        val attempt: Int,
        val attemptTimeout: Duration,
        val timestamp: TimeMark,
    ) : ConferenceState(isActive = true) {
        override fun getAutoToastMessage(context: Context): CharSequence {
            return context.getString(R.string.ConferenceState_Reconnecting)
        }
    }

    class Error(
        private val reason: Connector.ConnectorFailReason,
    ) : ConferenceState(isActive = false) {
        override fun getAutoToastMessage(context: Context): CharSequence {
            val match = FailReasonPattern.matchEntire(reason.name)
            val suffix = when (match != null) {
                true -> ": ${match.groupValues[1]}"
                else -> ""
            }
            return context.getString(R.string.ConferenceState_Fail) + suffix
        }
    }

    class Disconnected(
        val reason: Connector.ConnectorDisconnectReason,
    ) : ConferenceState(isActive = false)

    open fun getAutoToastMessage(context: Context): CharSequence? {
        return null
    }
}
