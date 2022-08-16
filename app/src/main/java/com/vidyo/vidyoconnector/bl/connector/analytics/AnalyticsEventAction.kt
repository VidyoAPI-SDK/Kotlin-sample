package com.vidyo.vidyoconnector.bl.connector.analytics

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector.ConnectorGoogleAnalyticsEventAction
import com.vidyo.vidyoconnector.R

enum class AnalyticsEventAction(
    @StringRes val textId: Int,
    val category: AnalyticsEventCategory,
    val jniValue: ConnectorGoogleAnalyticsEventAction,
) {
    All(
        textId = R.string.AnalyticsEventAction_All,
        category = AnalyticsEventCategory.None,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_All,
    ),
    Unknown(
        textId = R.string.AnalyticsEventAction_Unknown,
        category = AnalyticsEventCategory.None,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_Unknown,
    ),
    LoginSuccess(
        textId = R.string.AnalyticsEventAction_LoginSuccess,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginSuccess,
    ),
    LoginAttempt(
        textId = R.string.AnalyticsEventAction_LoginAttempt,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginAttempt,
    ),
    LoginFailedAuthentication(
        textId = R.string.AnalyticsEventAction_LoginFailedAuthentication,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginFailedAuthentication,
    ),
    LoginFailedConnect(
        textId = R.string.AnalyticsEventAction_LoginFailedConnect,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginFailedConnect,
    ),
    LoginFailedResponseTimeout(
        textId = R.string.AnalyticsEventAction_LoginFailedResponseTimeout,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginFailedResponseTimeout,
    ),
    LoginFailedMiscError(
        textId = R.string.AnalyticsEventAction_LoginFailedMiscError,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginFailedMiscError,
    ),
    LoginFailedWebProxyAuthRequired(
        textId = R.string.AnalyticsEventAction_LoginFailedWebProxyAuthRequired,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginFailedWebProxyAuthRequired,
    ),
    LoginFailedUnsupportedTenantVersion(
        textId = R.string.AnalyticsEventAction_LoginFailedUnsupportedTenantVersion,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_LoginFailedUnsupportedTenantVersion,
    ),
    UserTypeGuest(
        textId = R.string.AnalyticsEventAction_UserTypeGuest,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_UserTypeGuest,
    ),
    UserTypeRegularToken(
        textId = R.string.AnalyticsEventAction_UserTypeRegularToken,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_UserTypeRegularToken,
    ),
    UserTypeRegularPassword(
        textId = R.string.AnalyticsEventAction_UserTypeRegularPassword,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_UserTypeRegularPassword,
    ),
    UserTypeRegularSaml(
        textId = R.string.AnalyticsEventAction_UserTypeRegularSaml,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_UserTypeRegularSaml,
    ),
    UserTypeRegularExtData(
        textId = R.string.AnalyticsEventAction_UserTypeRegularExtData,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_UserTypeRegularExtdata,
    ),
    JoinConferenceSuccess(
        textId = R.string.AnalyticsEventAction_JoinConferenceSuccess,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceSuccess,
    ),
    JoinConferenceAttempt(
        textId = R.string.AnalyticsEventAction_JoinConferenceAttempt,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceAttempt,
    ),
    JoinConferenceReconnectRequests(
        textId = R.string.AnalyticsEventAction_JoinConferenceReconnectRequests,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceReconnectRequests,
    ),
    JoinConferenceFailedConnectionError(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedConnectionError,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceFailedConnectionError,
    ),
    JoinConferenceFailedWrongPin(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedWrongPin,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceFailedWrongPin,
    ),
    JoinConferenceFailedRoomFull(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedRoomFull,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceFailedRoomFull,
    ),
    JoinConferenceFailedRoomDisabled(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedRoomDisabled,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceFailedRoomDisabled,
    ),
    JoinConferenceFailedConferenceLocked(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedConferenceLocked,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceFailedConferenceLocked,
    ),
    JoinConferenceFailedUnknownError(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedUnknownError,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_JoinConferenceFailedUnknownError,
    ),
    ConferenceEndLeft(
        textId = R.string.AnalyticsEventAction_ConferenceEndLeft,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_ConferenceEndLeft,
    ),
    ConferenceEndBooted(
        textId = R.string.AnalyticsEventAction_ConferenceEndBooted,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_ConferenceEndBooted,
    ),
    ConferenceEndSignalingConnectionLost(
        textId = R.string.AnalyticsEventAction_ConferenceEndSignalingConnectionLost,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_ConferenceEndSignalingConnectionLost,
    ),
    ConferenceEndMediaConnectionLost(
        textId = R.string.AnalyticsEventAction_ConferenceEndMediaConnectionLost,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_ConferenceEndMediaConnectionLost,
    ),
    ConferenceEndUnknownError(
        textId = R.string.AnalyticsEventAction_ConferenceEndUnknownError,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_ConferenceEndUnknownError,
    ),
    InCallCodecVideoH264(
        textId = R.string.AnalyticsEventAction_InCallCodecVideoH264,
        category = AnalyticsEventCategory.InCallCodec,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_InCallCodecVideoH264,
    ),
    InCallCodecVideoH264SVC(
        textId = R.string.AnalyticsEventAction_InCallCodecVideoH264SVC,
        category = AnalyticsEventCategory.InCallCodec,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_InCallCodecVideoH264SVC,
    ),
    InCallCodecAudioSPEEXRED(
        textId = R.string.AnalyticsEventAction_InCallCodecAudioSPEEXRED,
        category = AnalyticsEventCategory.InCallCodec,
        jniValue = ConnectorGoogleAnalyticsEventAction.VIDYO_CONNECTORGOOGLEANALYTICSEVENTACTION_InCallCodecAudioSPEEXRED,
    );

    companion object {
        fun fromJniValue(jniValue: ConnectorGoogleAnalyticsEventAction): AnalyticsEventAction {
            return values().find { it.jniValue == jniValue } ?: Unknown
        }
    }
}
