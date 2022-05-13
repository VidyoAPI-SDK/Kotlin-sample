package com.vidyo.vidyoconnector.bl.connector.analytics

import org.json.JSONObject

sealed class AnalyticsInfo(val type: AnalyticsType) {

    object None : AnalyticsInfo(AnalyticsType.None) {
        override fun toJson() = ""
    }

    data class Google(val trackingId: String) : AnalyticsInfo(AnalyticsType.Google) {
        constructor(json: JSONObject) : this(
            trackingId = json.optString("trackingId").orEmpty(),
        )

        override fun toJson(): String {
            val json = JSONObject()
            json.put("trackingId", trackingId)
            return json.toString()
        }
    }

    data class VidyoInsight(val serverUrl: String) : AnalyticsInfo(AnalyticsType.VidyoInsight) {
        constructor(json: JSONObject) : this(
            serverUrl = json.optString("serverUrl").orEmpty(),
        )

        override fun toJson(): String {
            val json = JSONObject()
            json.put("serverUrl", serverUrl)
            return json.toString()
        }
    }

    abstract fun toJson(): String
}
