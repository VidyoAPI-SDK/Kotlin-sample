package com.vidyo.vidyoconnector.bl

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ProtocolHandlerArgs(
    val portal: String,
    val name: String,
    val roomKey: String,
    val roomPin: String,
    val enableDebug: Boolean,
    val autoJoin: Boolean,
    val allowReconnect: Boolean,
    val enableAutoReconnect: Boolean,
    val muteCamera: Boolean,
    val muteSpeaker: Boolean,
    val muteMicrophone: Boolean,
    val disableVideoOnLowBw: Boolean,
    val experimentalOptions: String,
)

object ProtocolHandler {
    private val channel = MutableStateFlow<ProtocolHandlerArgs?>(null)

    fun handle(intent: Intent) {
        val uri = intent.data
        if (uri?.scheme != "vidyoconnector") {
            return
        }

        val args = ProtocolHandlerArgs(
            portal = uri.getQueryParameter("portal").orEmpty(),
            name = uri.getQueryParameter("name").orEmpty(),
            roomKey = uri.getQueryParameter("roomKey").orEmpty(),
            roomPin = uri.getQueryParameter("roomPin").orEmpty(),
            enableDebug = uri.getBooleanQueryParameter("enableDebug", false),
            autoJoin = uri.getBooleanQueryParameter("autoJoin", false),
            allowReconnect = uri.getBooleanQueryParameter("allowReconnect", true),
            enableAutoReconnect = uri.getBooleanQueryParameter("enableAutoReconnect", false),
            muteCamera = uri.getBooleanQueryParameter("cameraPrivacy", false),
            muteSpeaker = uri.getBooleanQueryParameter("speakerPrivacy", false),
            muteMicrophone = uri.getBooleanQueryParameter("microphonePrivacy", false),
            disableVideoOnLowBw = uri.getBooleanQueryParameter("disableVideoOnLowBw", false),
            experimentalOptions = uri.getQueryParameter("experimentalOptions").orEmpty(),
        )

        intent.data = null
        channel.value = args
    }

    fun track(): Flow<ProtocolHandlerArgs?> {
        return channel
    }

    fun consume(): ProtocolHandlerArgs? {
        val args = channel.value
        channel.value = null
        return args
    }
}
