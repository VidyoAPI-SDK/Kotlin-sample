package com.vidyo.vidyoconnector.bl.connector

import android.content.Context
import com.vidyo.VidyoClient.Connector.Connector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

class ConnectorScope(
    val context: Context,
    val connector: Connector,
    parent: Job? = null,
) : CoroutineScope {
    private val job = SupervisorJob(parent)

    val dispatcher = Dispatchers.Main.immediate

    override val coroutineContext = job + dispatcher

    fun run(block: () -> Unit) {
        dispatcher.dispatch(EmptyCoroutineContext) {
            block()
        }
    }

    fun newChildScope() = ConnectorScope(
        context = context,
        connector = connector,
        parent = job,
    )
}
