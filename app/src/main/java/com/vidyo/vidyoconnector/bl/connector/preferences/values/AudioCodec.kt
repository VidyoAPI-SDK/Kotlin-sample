package com.vidyo.vidyoconnector.bl.connector.preferences.values

import androidx.annotation.StringRes
import com.vidyo.vidyoconnector.R

enum class AudioCodec(
    @StringRes val textId: Int,
    val jniValue: String,
) {
    Opus(
        textId = R.string.AudioCodec_Opus,
        jniValue = "OPUS",
    ),
    OpusRed(
        textId = R.string.AudioCodec_OpusRed,
        jniValue = "OPUS RED",
    ),
    SpeexRed(
        textId = R.string.AudioCodec_SpeexRed,
        jniValue = "SPEEX RED",
    );

    companion object {
        inline fun fromOrdinal(ordinal: Int, fallback: () -> AudioCodec): AudioCodec {
            return values().find { it.ordinal == ordinal } ?: fallback()
        }

        inline fun fromJniValue(value: String, fallback: () -> AudioCodec): AudioCodec {
            return values().find { it.jniValue == value } ?: fallback()
        }
    }
}
