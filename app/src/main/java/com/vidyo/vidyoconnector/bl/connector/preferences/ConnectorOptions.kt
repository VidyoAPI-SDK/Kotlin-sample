package com.vidyo.vidyoconnector.bl.connector.preferences

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.bl.connector.preferences.values.AudioCodec
import org.json.JSONObject

class ConnectorOptions(private val connector: Connector) {

    var preferredAudioCodec = AudioCodec.Opus
        private set

    var audioPacketInterval = 40
        private set

    var audioPacketLossPercentage = 10
        private set

    var audioBitrateMultiplier = 2
        private set

    init {
        val json = try {
            JSONObject(connector.options)
        } catch (e: Exception) {
            JSONObject()
        }

        val value = json.optString("preferredAudioCodec", "")
        preferredAudioCodec = AudioCodec.fromJniValue(value) { preferredAudioCodec }

        audioPacketInterval = json.optInt(
            "AudioPacketInterval",
            audioPacketInterval,
        )

        audioPacketLossPercentage = json.optInt(
            "AudioPacketLossPercentage",
            audioPacketLossPercentage
        )

        audioBitrateMultiplier = json.optInt(
            "AudioBitrateMultiplier",
            audioBitrateMultiplier,
        )
    }

    fun setPreferredAudioCodec(value: AudioCodec): Boolean {
        val result = connector.setOptions("""{"preferredAudioCodec":"${value.jniValue}"}""")
        if (result) {
            preferredAudioCodec = value
        }
        return result
    }

    fun setAudioPacketInterval(value: Int): Boolean {
        val result = connector.setOptions("""{"AudioPacketInterval":$value}""")
        if (result) {
            audioPacketInterval = value
        }
        return result
    }

    fun setAudioPacketLossPercentage(value: Int): Boolean {
        val result = connector.setOptions("""{"AudioPacketLossPercentage":$value}""")
        if (result) {
            audioPacketLossPercentage = value
        }
        return result
    }

    fun setAudioBitrateMultiplier(value: Int): Boolean {
        val result = connector.setOptions("""{"AudioBitrateMultiplier":$value}""")
        if (result) {
            audioBitrateMultiplier = value
        }
        return result
    }
}
