package com.vidyo.vidyoconnector.bl.connector.conference

import android.content.Intent
import androidx.core.content.ContextCompat
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.ui.utils.showToast
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.logD
import com.vidyo.vidyoconnector.utils.logE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConferenceManager(private val scope: ConnectorScope) {
    companion object : Loggable.Tag("ConferenceManager")

    private val conferenceState = MutableStateFlow(Conference.Null)

    val conference = conferenceState.asStateFlow()

    init {
        scope.connector.registerReconnectEventListener(ReconnectEventListener())

        conferenceState.map { it.state.isActive }.distinctUntilChanged().collectInScope(scope) {
            val intent = Intent(scope.context, ConferenceService::class.java)
            when (it) {
                true -> ContextCompat.startForegroundService(scope.context, intent)
                else -> scope.context.stopService(intent)
            }
        }

        conferenceState.collectInScope(scope) {
            logD { "conference = $it" }
            showToast(it.state.getAutoToastMessage(scope.context))
        }
    }

    fun join(info: ConferenceJoinInfo) = scope.run {
        if (conferenceState.value.state.isActive) {
            return@run
        }

        conferenceState.value = Conference(
            joinInfo = info,
            state = ConferenceState.Joining,
        )

        logD { "join: info = $info" }

        scope.launch {
            val result = info.join(scope.connector, ConnectListener())
            if (!result) {
                logE { "join: failed" }
                conferenceState.value = Conference.Null
            }
        }
    }

    fun disconnect() = scope.run {
        logD { "disconnect" }
        scope.connector.disconnect()
    }

    private inner class ConnectListener : Connector.IConnect {
        override fun onSuccess() = scope.run {
            logD { "onSuccess" }
            conferenceState.value = conferenceState.value.copy(state = ConferenceState.Joined)
        }

        override fun onFailure(reason: Connector.ConnectorFailReason) = scope.run {
            logD { "onFailure: reason = $reason" }
            conferenceState.value = conferenceState.value.copy(state = ConferenceState.Error(reason))
        }

        override fun onDisconnected(reason: Connector.ConnectorDisconnectReason) = scope.run {
            logD { "onDisconnected: reason = $reason" }
            conferenceState.value = conferenceState.value.copy(state = ConferenceState.Disconnected(reason))
        }
    }

    private inner class ReconnectEventListener : Connector.IRegisterReconnectEventListener {
        override fun onReconnecting(
            attempt: Int,
            attemptTimeout: Int,
            reason: Connector.ConnectorFailReason,
        ) = scope.run {
            logD { "onReconnecting: reason = $reason" }
            conferenceState.value = conferenceState.value.copy(state = ConferenceState.Reconnecting)
        }

        override fun onReconnected() = scope.run {
            logD { "onReconnected" }
            conferenceState.value = conferenceState.value.copy(state = ConferenceState.Joined)
        }

        override fun onConferenceLost(reason: Connector.ConnectorFailReason) = scope.run {
            logD { "onConferenceLost: reason = $reason" }
            conferenceState.value = conferenceState.value.copy(state = ConferenceState.Error(reason))
        }
    }
}
