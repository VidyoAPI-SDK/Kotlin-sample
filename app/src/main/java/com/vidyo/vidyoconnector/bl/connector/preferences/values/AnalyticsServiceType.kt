package com.vidyo.vidyoconnector.bl.connector.preferences.values

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Connector.Connector.ConnectorAnalyticsServiceType.*
import com.vidyo.vidyoconnector.R

enum class AnalyticsServiceType(
    @StringRes val textId: Int,
    val jniValue: Connector.ConnectorAnalyticsServiceType,
) {
    None(
        textId = R.string.AnalyticsServiceType_None,
        jniValue = VIDYO_CONNECTORANALYTICSSERVICETYPE_None
    ),
    Google(
        textId = R.string.AnalyticsServiceType_Google,
        jniValue = VIDYO_CONNECTORANALYTICSSERVICETYPE_Google,
    ),
    VidyoInsight(
        textId = R.string.AnalyticsServiceType_VidyoInsights,
        jniValue = VIDYO_CONNECTORANALYTICSSERVICETYPE_VidyoInsights,
    );

    companion object {
        val Default = Google

        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: None
    }
}
