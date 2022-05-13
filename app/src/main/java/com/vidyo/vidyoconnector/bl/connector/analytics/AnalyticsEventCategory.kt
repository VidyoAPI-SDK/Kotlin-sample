package com.vidyo.vidyoconnector.bl.connector.analytics

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector.ConnectorAnalyticsEventCategory
import com.vidyo.vidyoconnector.R

enum class AnalyticsEventCategory(
    @StringRes val textId: Int,
    val jniValue: ConnectorAnalyticsEventCategory,
) {
    None(
        textId = R.string.AnalyticsEventCategory_None,
        jniValue = ConnectorAnalyticsEventCategory.VIDYO_CONNECTORANALYTICSEVENTCATEGORY_None,
    ),
    Login(
        textId = R.string.AnalyticsEventCategory_Login,
        jniValue = ConnectorAnalyticsEventCategory.VIDYO_CONNECTORANALYTICSEVENTCATEGORY_Login,
    ),
    UserType(
        textId = R.string.AnalyticsEventCategory_UserType,
        jniValue = ConnectorAnalyticsEventCategory.VIDYO_CONNECTORANALYTICSEVENTCATEGORY_UserType,
    ),
    JoinConference(
        textId = R.string.AnalyticsEventCategory_JoinConference,
        jniValue = ConnectorAnalyticsEventCategory.VIDYO_CONNECTORANALYTICSEVENTCATEGORY_JoinConference,
    ),
    ConferenceEnd(
        textId = R.string.AnalyticsEventCategory_ConferenceEnd,
        jniValue = ConnectorAnalyticsEventCategory.VIDYO_CONNECTORANALYTICSEVENTCATEGORY_ConferenceEnd,
    ),
    InCallCodec(
        textId = R.string.AnalyticsEventCategory_InCallCodec,
        jniValue = ConnectorAnalyticsEventCategory.VIDYO_CONNECTORANALYTICSEVENTCATEGORY_InCallCodec,
    );

    companion object {
        fun fromJniValue(jniValue: ConnectorAnalyticsEventCategory): AnalyticsEventCategory {
            return values().find { it.jniValue == jniValue } ?: None
        }
    }
}
