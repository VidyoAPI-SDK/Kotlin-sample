package com.vidyo.vidyoconnector.bl.connector.preferences.values

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector.ConnectorPreferredAudioCodec
import com.vidyo.vidyoconnector.R

enum class AudioCodec(
    @StringRes val textId: Int,
    val jniValue: ConnectorPreferredAudioCodec,
) {
    Unknown(
        textId = R.string.AudioCodec_Unknown,
        jniValue = ConnectorPreferredAudioCodec.VIDYO_CONNECTORPREFERREDAUDIOCODEC_Unknown,
    ),
    Opus(
        textId = R.string.AudioCodec_Opus,
        jniValue = ConnectorPreferredAudioCodec.VIDYO_CONNECTORPREFERREDAUDIOCODEC_Opus,
    ),
    OpusRed(
        textId = R.string.AudioCodec_OpusRed,
        jniValue = ConnectorPreferredAudioCodec.VIDYO_CONNECTORPREFERREDAUDIOCODEC_OpusRed,
    ),
    SpeexRed(
        textId = R.string.AudioCodec_SpeexRed,
        jniValue = ConnectorPreferredAudioCodec.VIDYO_CONNECTORPREFERREDAUDIOCODEC_SpeexRed,
    );

    companion object {
        inline fun fromOrdinal(
            ordinal: Int,
            fallback: () -> AudioCodec = { Unknown },
        ): AudioCodec {
            return values().find { it.ordinal == ordinal } ?: fallback()
        }

        inline fun fromJniValue(
            value: ConnectorPreferredAudioCodec,
            fallback: () -> AudioCodec = { Unknown },
        ): AudioCodec {
            return values().find { it.jniValue == value } ?: fallback()
        }
    }
}
