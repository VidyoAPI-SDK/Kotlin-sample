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

class ConferenceManager(private val scope: ConnectorScope) {
    companion object : Loggable.Tag("ConferenceManager")

    private val stateMutable = MutableStateFlow<ConferenceState>(ConferenceState.Idle)

    val state = stateMutable.asStateFlow()

    init {
        scope.connector.registerReconnectEventListener(ReconnectEventListener())

        state.map { it.isActive }.distinctUntilChanged().collectInScope(scope) {
            val intent = Intent(scope.context, ConferenceService::class.java)
            when (it) {
                true -> ContextCompat.startForegroundService(scope.context, intent)
                else -> scope.context.stopService(intent)
            }
        }

        state.collectInScope(scope) {
            logD { "state = $it" }
            showToast(it.getAutoToastMessage(scope.context))
        }
    }

    fun join(info: ConferenceJoinInfo) = scope.run {
        if (state.value.isActive) {
            return@run
        }

        stateMutable.value = ConferenceState.Joining

        logD { "join: info = $info" }

        val result = info.join(scope.connector, ConnectListener())
        if (!result) {
            logE { "join: failed" }
            stateMutable.value = ConferenceState.Idle
        }
    }

    fun disconnect() = scope.run {
        logD { "disconnect" }
        scope.connector.disconnect()
    }

    private inner class ConnectListener : Connector.IConnect {
        override fun onSuccess() = scope.run {
            logD { "onSuccess" }
            stateMutable.value = ConferenceState.Joined
        }

        override fun onFailure(reason: Connector.ConnectorFailReason) = scope.run {
            logD { "onFailure: reason = $reason" }
            stateMutable.value = ConferenceState.Error(reason)
        }

        override fun onDisconnected(reason: Connector.ConnectorDisconnectReason) = scope.run {
            logD { "onDisconnected: reason = $reason" }
            stateMutable.value = ConferenceState.Disconnected(reason)
        }
    }

    private inner class ReconnectEventListener : Connector.IRegisterReconnectEventListener {
        override fun onReconnecting(
            attempt: Int,
            attemptTimeout: Int,
            reason: Connector.ConnectorFailReason,
        ) = scope.run {
            logD { "onReconnecting: reason = $reason" }
            stateMutable.value = ConferenceState.Reconnecting
        }

        override fun onReconnected() = scope.run {
            logD { "onReconnected" }
            stateMutable.value = ConferenceState.Joined
        }

        override fun onConferenceLost(reason: Connector.ConnectorFailReason) = scope.run {
            logD { "onConferenceLost: reason = $reason" }
            stateMutable.value = ConferenceState.Error(reason)
        }
    }
}
