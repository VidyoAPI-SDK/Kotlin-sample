package com.vidyo.vidyoconnector.bl.connector.preferences

import android.content.Context
import android.content.SharedPreferences
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.preferences.values.AudioCodec
import com.vidyo.vidyoconnector.bl.connector.preferences.values.Bitrate
import com.vidyo.vidyoconnector.bl.connector.preferences.values.CpuTradeOffProfile

class PreferencesManager(private val scope: ConnectorScope) {
    private val shared = scope.context.getSharedPreferences("main", Context.MODE_PRIVATE)

    val cpuTradeOffProfile = createRuntimeProperty(
        read = { CpuTradeOffProfile.fromJniValue(scope.connector.cpuTradeOffProfile) },
        write = { scope.connector.cpuTradeOffProfile = it.jniValue },
    )

    val autoReconnect = createRuntimeProperty(
        read = { scope.connector.autoReconnect },
        write = { scope.connector.autoReconnect = it },
    )

    val autoReconnectMaxAttempts = createRuntimeProperty(
        read = { scope.connector.autoReconnectMaxAttempts },
        write = { scope.connector.autoReconnectMaxAttempts = it },
    )

    val autoReconnectAttemptBackOff = createRuntimeProperty(
        read = { scope.connector.autoReconnectAttemptBackOff },
        write = { scope.connector.autoReconnectAttemptBackOff = it },
    )

    val audioCodec = createRuntimeProperty(
        read = { AudioCodec.fromJniValue(scope.connector.preferredAudioCodec) },
        write = { scope.connector.preferredAudioCodec = it.jniValue },
    )

    val audioPacketInterval = createRuntimeProperty(
        read = { scope.connector.audioPacketInterval },
        write = { scope.connector.audioPacketInterval = it },
    )

    val audioPacketLossPercentage = createRuntimeProperty(
        read = { scope.connector.audioPacketLossPercentage },
        write = { scope.connector.audioPacketLossPercentage = it },
    )

    val audioBitrateMultiplier = createRuntimeProperty(
        read = { scope.connector.audioBitrateMultiplier },
        write = { scope.connector.audioBitrateMultiplier = it },
    )

    val disableVideoOnLowBandwidth = createRuntimeProperty(
        read = { scope.connector.disableVideoOnLowBandwidth },
        write = { scope.connector.disableVideoOnLowBandwidth = it },
    )

    val disableVideoOnLowBandwidthResponseTime = createRuntimeProperty(
        read = { scope.connector.disableVideoOnLowBandwidthResponseTime },
        write = { scope.connector.disableVideoOnLowBandwidthResponseTime = it },
    )

    val disableVideoOnLowBandwidthSampleTime = createRuntimeProperty(
        read = { scope.connector.disableVideoOnLowBandwidthSampleTime },
        write = { scope.connector.disableVideoOnLowBandwidthSampleTime = it },
    )

    val disableVideoOnLowBandwidthThreshold = createRuntimeProperty(
        read = { scope.connector.disableVideoOnLowBandwidthThreshold },
        write = { scope.connector.disableVideoOnLowBandwidthThreshold = it },
    )

    val disableVideoOnLowBandwidthAudioStreams = createRuntimeProperty(
        read = { scope.connector.disableVideoOnLowBandwidthAudioStreams },
        write = { scope.connector.disableVideoOnLowBandwidthAudioStreams = it },
    )

    val maxSendBitRate = createRuntimeProperty(
        read = { Bitrate.fromJniValue(scope.connector.maxSendBitRate) },
        write = { scope.connector.maxSendBitRate = Bitrate.toJniValue(it) },
    )

    val maxReceiveBitRate = createRuntimeProperty(
        read = { Bitrate.fromJniValue(scope.connector.maxReceiveBitRate) },
        write = { scope.connector.maxReceiveBitRate = Bitrate.toJniValue(it) },
    )

    fun <T> createRuntimeProperty(read: () -> T, write: (T) -> Unit): RuntimeProperty<T> {
        return RuntimeProperty(read(), write)
    }

    fun <T> createPreferencesProperty(
        key: String,
        read: SharedPreferences.(String) -> T,
        write: SharedPreferences.Editor.(String, T) -> Unit,
    ) = PreferencesProperty(shared, key, read, write)
}
