package com.vidyo.vidyoconnector.bl.connector.preferences

import android.content.Context
import android.content.SharedPreferences
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.media.local.camera.LocalCameraConstraints
import com.vidyo.vidyoconnector.bl.connector.preferences.values.AudioCodec
import com.vidyo.vidyoconnector.bl.connector.preferences.values.Bitrate
import com.vidyo.vidyoconnector.bl.connector.preferences.values.CpuTradeOffProfile

class PreferencesManager(scope: ConnectorScope) {
    private val shared = scope.context.getSharedPreferences("main", Context.MODE_PRIVATE)

    val numberOfParticipants = createPreferencesProperty(
        key = "number_of_participants",
        read = { getInt(it, 3) },
        write = { key, value -> putInt(key, value) },
    )

    val cpuTradeOffProfile = createConnectorProperty(
        key = "cpu_profile",
        read = {
            CpuTradeOffProfile.fromOrdinal(getInt(it, -1)) {
                CpuTradeOffProfile.fromJniValue(scope.connector.cpuTradeOffProfile)
            }
        },
        write = { key, value -> putInt(key, value.ordinal) },
        set = { scope.connector.setCpuTradeOffProfile(it.jniValue) },
        get = { CpuTradeOffProfile.fromJniValue(scope.connector.cpuTradeOffProfile) },
    )

    val autoReconnect = createConnectorProperty(
        key = "auto_reconnect",
        read = { getBoolean(it, scope.connector.autoReconnect) },
        write = { key, value -> putBoolean(key, value) },
        set = { scope.connector.setAutoReconnect(it) },
        get = { scope.connector.autoReconnect },
    )

    val autoReconnectMaxAttempts = createConnectorProperty(
        key = "auto_reconnect_max_attempts",
        read = { getInt(it, scope.connector.autoReconnectMaxAttempts) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setAutoReconnectMaxAttempts(it) },
        get = { scope.connector.autoReconnectMaxAttempts },
    )

    val autoReconnectAttemptBackOff = createConnectorProperty(
        key = "auto_reconnect_attempt_back_off",
        read = { getInt(it, scope.connector.autoReconnectAttemptBackOff) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setAutoReconnectAttemptBackOff(it) },
        get = { scope.connector.autoReconnectAttemptBackOff },
    )

    val audioCodec = createConnectorProperty(
        key = "preferred_audio_codec",
        read = {
            AudioCodec.fromOrdinal(getInt(it, -1)) {
                AudioCodec.fromJniValue(scope.connector.preferredAudioCodec)
            }
        },
        write = { key, value -> putInt(key, value.ordinal) },
        set = { scope.connector.setPreferredAudioCodec(it.jniValue) },
        get = { AudioCodec.fromJniValue(scope.connector.preferredAudioCodec) },
    )

    val audioPacketInterval = createConnectorProperty(
        key = "audio_packet_interval",
        read = { getInt(it, scope.connector.audioPacketInterval) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setAudioPacketInterval(it) },
        get = { scope.connector.audioPacketInterval },
    )

    val audioPacketLossPercentage = createConnectorProperty(
        key = "audio_packet_loss_percentage",
        read = { getInt(it, scope.connector.audioPacketLossPercentage) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setAudioPacketLossPercentage(it) },
        get = { scope.connector.audioPacketLossPercentage },
    )

    val audioBitrateMultiplier = createConnectorProperty(
        key = "audio_bitrate_multiplier",
        read = { getInt(it, scope.connector.audioBitrateMultiplier) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setAudioBitrateMultiplier(it) },
        get = { scope.connector.audioBitrateMultiplier },
    )

    val localCameraConstraints = createPreferencesProperty(
        key = "local_camera_constraints",
        read = { LocalCameraConstraints.fromJson(getString(it, "").orEmpty()) },
        write = { key, value ->
            when (value != null) {
                true -> putString(key, value.toJson())
                else -> remove(key)
            }
        },
    )

    val disableVideoOnLowBandwidth = createConnectorProperty(
        key = "disable_video_on_low_bandwidth",
        read = { getBoolean(it, scope.connector.disableVideoOnLowBandwidth) },
        write = { key, value -> putBoolean(key, value) },
        set = { scope.connector.setDisableVideoOnLowBandwidth(it) },
        get = { scope.connector.disableVideoOnLowBandwidth },
    )

    val disableVideoOnLowBandwidthResponseTime = createConnectorProperty(
        key = "disable_video_on_low_bandwidth_response_time",
        read = { getInt(it, scope.connector.disableVideoOnLowBandwidthResponseTime) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setDisableVideoOnLowBandwidthResponseTime(it) },
        get = { scope.connector.disableVideoOnLowBandwidthResponseTime },
    )

    val disableVideoOnLowBandwidthSampleTime = createConnectorProperty(
        key = "disable_video_on_low_bandwidth_sample_time",
        read = { getInt(it, scope.connector.disableVideoOnLowBandwidthSampleTime) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setDisableVideoOnLowBandwidthSampleTime(it) },
        get = { scope.connector.disableVideoOnLowBandwidthSampleTime },
    )

    val disableVideoOnLowBandwidthThreshold = createConnectorProperty(
        key = "disable_video_on_low_bandwidth_threshold",
        read = { getInt(it, scope.connector.disableVideoOnLowBandwidthThreshold) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setDisableVideoOnLowBandwidthThreshold(it) },
        get = { scope.connector.disableVideoOnLowBandwidthThreshold },
    )

    val disableVideoOnLowBandwidthAudioStreams = createConnectorProperty(
        key = "disable_video_on_low_bandwidth_audio_streams",
        read = { getInt(it, scope.connector.disableVideoOnLowBandwidthAudioStreams) },
        write = { key, value -> putInt(key, value) },
        set = { scope.connector.setDisableVideoOnLowBandwidthAudioStreams(it) },
        get = { scope.connector.disableVideoOnLowBandwidthAudioStreams },
    )

    val maxSendBitRate = createConnectorProperty(
        key = "max_send_bit_rate",
        read = { getLong(it, Bitrate.fromJniValue(scope.connector.maxSendBitRate)) },
        write = { key, value -> putLong(key, value) },
        set = { scope.connector.setMaxSendBitRate(Bitrate.toJniValue(it)) },
        get = { Bitrate.fromJniValue(scope.connector.maxSendBitRate) }
    )

    val maxReceiveBitRate = createConnectorProperty(
        key = "max_receive_bit_rate",
        read = { getLong(it, Bitrate.fromJniValue(scope.connector.maxReceiveBitRate)) },
        write = { key, value -> putLong(key, value) },
        set = { scope.connector.setMaxReceiveBitRate(Bitrate.toJniValue(it)) },
        get = { Bitrate.fromJniValue(scope.connector.maxReceiveBitRate) }
    )

    fun <T> createPreferencesProperty(
        key: String,
        read: SharedPreferences.(String) -> T,
        write: SharedPreferences.Editor.(String, T) -> Unit,
    ) = PreferencesProperty(
        shared,
        key,
        read,
        write,
    )

    fun <T> createConnectorProperty(
        key: String,
        read: SharedPreferences.(String) -> T,
        write: SharedPreferences.Editor.(String, T) -> Unit,
        get: (T) -> T = { it },
        set: (T) -> Boolean = { true },
    ) = ConnectorProperty(
        shared,
        key,
        read,
        write,
        get = get,
        set = set,
    )
}
