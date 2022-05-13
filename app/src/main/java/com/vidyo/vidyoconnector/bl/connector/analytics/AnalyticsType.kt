package com.vidyo.vidyoconnector.bl.connector.analytics

import com.vidyo.VidyoClient.Connector.Connector.ConnectorAnalyticsServiceType
import com.vidyo.vidyoconnector.R
import org.json.JSONObject

enum class AnalyticsType(val textId: Int, val jniValue: ConnectorAnalyticsServiceType) {
    None(
        textId = R.string.AnalyticsServiceType_None,
        jniValue = ConnectorAnalyticsServiceType.VIDYO_CONNECTORANALYTICSSERVICETYPE_None,
    ) {
        override fun fromJson(json: String) = AnalyticsInfo.None
    },
    Google(
        textId = R.string.AnalyticsServiceType_Google,
        jniValue = ConnectorAnalyticsServiceType.VIDYO_CONNECTORANALYTICSSERVICETYPE_Google,
    ) {
        override fun fromJson(json: String): AnalyticsInfo.Google {
            return try {
                AnalyticsInfo.Google(json = JSONObject(json))
            } catch (e: Exception) {
                AnalyticsInfo.Google(trackingId = "")
            }
        }
    },
    VidyoInsight(
        textId = R.string.AnalyticsServiceType_VidyoInsights,
        jniValue = ConnectorAnalyticsServiceType.VIDYO_CONNECTORANALYTICSSERVICETYPE_VidyoInsights,
    ) {
        override fun fromJson(json: String): AnalyticsInfo.VidyoInsight {
            return try {
                AnalyticsInfo.VidyoInsight(json = JSONObject(json))
            } catch (e: Exception) {
                AnalyticsInfo.VidyoInsight(serverUrl = "")
            }
        }
    };

    companion object {
        inline fun fromOrdinal(
            ordinal: Int,
            fallback: () -> AnalyticsType = { None },
        ): AnalyticsType {
            return values().getOrNull(ordinal) ?: fallback()
        }
    }

    abstract fun fromJson(json: String): AnalyticsInfo
}
