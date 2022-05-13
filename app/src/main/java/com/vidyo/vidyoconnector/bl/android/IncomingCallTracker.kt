package com.vidyo.vidyoconnector.bl.android

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

enum class IncomingCallState {
    Idle, Ringing, Active;

    companion object {
        fun track(context: Context): Flow<IncomingCallState> {
            val manager = context.getSystemService<TelephonyManager>() ?: return flowOf(Idle)

            val flow = if (Build.VERSION.SDK_INT >= 31) {
                trackIncomingCallStateV31(manager)
            } else {
                trackIncomingCallStateBase(manager)
            }

            return flow.mapNotNull {
                when (it) {
                    TelephonyManager.CALL_STATE_IDLE -> Idle
                    TelephonyManager.CALL_STATE_RINGING -> Ringing
                    TelephonyManager.CALL_STATE_OFFHOOK -> Active
                    else -> null
                }
            }
        }
    }
}

@TargetApi(31)
private fun trackIncomingCallStateV31(manager: TelephonyManager) = channelFlow {
    val executor = Dispatchers.Main.immediate.asExecutor()
    val callback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            trySend(state)
        }
    }
    manager.registerTelephonyCallback(executor, callback)
    awaitClose {
        manager.unregisterTelephonyCallback(callback)
    }
}

@Suppress("DEPRECATION")
private fun trackIncomingCallStateBase(manager: TelephonyManager) = channelFlow {
    val callback = withContext(Dispatchers.Main.immediate) {
        object : android.telephony.PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                trySend(state)
            }
        }
    }

    manager.listen(callback, android.telephony.PhoneStateListener.LISTEN_CALL_STATE)
    awaitClose {
        manager.listen(callback, android.telephony.PhoneStateListener.LISTEN_NONE)
    }
}
