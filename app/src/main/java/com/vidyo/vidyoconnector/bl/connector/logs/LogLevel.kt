package com.vidyo.vidyoconnector.bl.connector.logs

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.R

enum class LogLevel(@StringRes val textId: Int, val jniValue: Connector.ConnectorLogLevel) {
    Debug(
        textId = R.string.LogLevel_Debug,
        jniValue = Connector.ConnectorLogLevel.VIDYO_CONNECTORLOGLEVEL_DEBUG,
    ),
    Production(
        textId = R.string.LogLevel_Production,
        jniValue = Connector.ConnectorLogLevel.VIDYO_CONNECTORLOGLEVEL_PRODUCTION,
    ),
    Advanced(
        textId = R.string.LogLevel_Advanced,
        jniValue = Connector.ConnectorLogLevel.VIDYO_CONNECTORLOGLEVEL_INVALID,
    );

    companion object {
        inline fun fromOrdinal(ordinal: Int, fallback: () -> LogLevel): LogLevel {
            return values().find { it.ordinal == ordinal } ?: fallback()
        }

        inline fun fromJniValue(
            value: Connector.ConnectorLogLevel,
            fallback: () -> LogLevel,
        ): LogLevel {
            return values().find { it.jniValue == value } ?: fallback()
        }
    }
}
