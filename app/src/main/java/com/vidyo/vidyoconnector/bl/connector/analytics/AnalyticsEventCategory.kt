package com.vidyo.vidyoconnector.bl.connector.analytics

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector.ConnectorGoogleAnalyticsEventCategory
import com.vidyo.vidyoconnector.R

enum class AnalyticsEventCategory(
    @StringRes val textId: Int,
    val jniValue: ConnectorGoogleAnalyticsEventCategory,
) {
    None(
        textId = R.string.AnalyticsEventCategory_None,
        jniValue = ConnectorGoogleAnalyticsEventCategory.VIDYO_CONNECTORGOOGLEANALYTICSEVENTCATEGORY_None,
    ),
    Login(
        textId = R.string.AnalyticsEventCategory_Login,
        jniValue = ConnectorGoogleAnalyticsEventCategory.VIDYO_CONNECTORGOOGLEANALYTICSEVENTCATEGORY_Login,
    ),
    UserType(
        textId = R.string.AnalyticsEventCategory_UserType,
        jniValue = ConnectorGoogleAnalyticsEventCategory.VIDYO_CONNECTORGOOGLEANALYTICSEVENTCATEGORY_UserType,
    ),
    JoinConference(
        textId = R.string.AnalyticsEventCategory_JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventCategory.VIDYO_CONNECTORGOOGLEANALYTICSEVENTCATEGORY_JoinConference,
    ),
    ConferenceEnd(
        textId = R.string.AnalyticsEventCategory_ConferenceEnd,
        jniValue = ConnectorGoogleAnalyticsEventCategory.VIDYO_CONNECTORGOOGLEANALYTICSEVENTCATEGORY_ConferenceEnd,
    ),
    InCallCodec(
        textId = R.string.AnalyticsEventCategory_InCallCodec,
        jniValue = ConnectorGoogleAnalyticsEventCategory.VIDYO_CONNECTORGOOGLEANALYTICSEVENTCATEGORY_InCallCodec,
    );

    companion object {
        fun fromJniValue(jniValue: ConnectorGoogleAnalyticsEventCategory): AnalyticsEventCategory {
            return values().find { it.jniValue == jniValue } ?: None
        }
    }
}
