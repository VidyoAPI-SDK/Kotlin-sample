package com.vidyo.vidyoconnector.bl.connector.analytics

import androidx.annotation.StringRes
import com.vidyo.VidyoClient.Connector.Connector.ConnectorAnalyticsEventAction
import com.vidyo.vidyoconnector.R

enum class AnalyticsEventAction(
    @StringRes val textId: Int,
    val category: AnalyticsEventCategory,
    val jniValue: ConnectorAnalyticsEventAction,
) {
    All(
        textId = R.string.AnalyticsEventAction_All,
        category = AnalyticsEventCategory.None,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_All,
    ),
    Unknown(
        textId = R.string.AnalyticsEventAction_Unknown,
        category = AnalyticsEventCategory.None,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_Unknown,
    ),
    LoginSuccess(
        textId = R.string.AnalyticsEventAction_LoginSuccess,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginSuccess,
    ),
    LoginAttempt(
        textId = R.string.AnalyticsEventAction_LoginAttempt,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginAttempt,
    ),
    LoginFailedAuthentication(
        textId = R.string.AnalyticsEventAction_LoginFailedAuthentication,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginFailedAuthentication,
    ),
    LoginFailedConnect(
        textId = R.string.AnalyticsEventAction_LoginFailedConnect,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginFailedConnect,
    ),
    LoginFailedResponseTimeout(
        textId = R.string.AnalyticsEventAction_LoginFailedResponseTimeout,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginFailedResponseTimeout,
    ),
    LoginFailedMiscError(
        textId = R.string.AnalyticsEventAction_LoginFailedMiscError,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginFailedMiscError,
    ),
    LoginFailedWebProxyAuthRequired(
        textId = R.string.AnalyticsEventAction_LoginFailedWebProxyAuthRequired,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginFailedWebProxyAuthRequired,
    ),
    LoginFailedUnsupportedTenantVersion(
        textId = R.string.AnalyticsEventAction_LoginFailedUnsupportedTenantVersion,
        category = AnalyticsEventCategory.Login,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_LoginFailedUnsupportedTenantVersion,
    ),
    UserTypeGuest(
        textId = R.string.AnalyticsEventAction_UserTypeGuest,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_UserTypeGuest,
    ),
    UserTypeRegularToken(
        textId = R.string.AnalyticsEventAction_UserTypeRegularToken,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_UserTypeRegularToken,
    ),
    UserTypeRegularPassword(
        textId = R.string.AnalyticsEventAction_UserTypeRegularPassword,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_UserTypeRegularPassword,
    ),
    UserTypeRegularSaml(
        textId = R.string.AnalyticsEventAction_UserTypeRegularSaml,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_UserTypeRegularSaml,
    ),
    UserTypeRegularExtData(
        textId = R.string.AnalyticsEventAction_UserTypeRegularExtData,
        category = AnalyticsEventCategory.UserType,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_UserTypeRegularExtdata,
    ),
    JoinConferenceSuccess(
        textId = R.string.AnalyticsEventAction_JoinConferenceSuccess,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceSuccess,
    ),
    JoinConferenceAttempt(
        textId = R.string.AnalyticsEventAction_JoinConferenceAttempt,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceAttempt,
    ),
    JoinConferenceReconnectRequests(
        textId = R.string.AnalyticsEventAction_JoinConferenceReconnectRequests,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceReconnectRequests,
    ),
    JoinConferenceFailedConnectionError(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedConnectionError,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceFailedConnectionError,
    ),
    JoinConferenceFailedWrongPin(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedWrongPin,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceFailedWrongPin,
    ),
    JoinConferenceFailedRoomFull(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedRoomFull,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceFailedRoomFull,
    ),
    JoinConferenceFailedRoomDisabled(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedRoomDisabled,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceFailedRoomDisabled,
    ),
    JoinConferenceFailedConferenceLocked(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedConferenceLocked,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceFailedConferenceLocked,
    ),
    JoinConferenceFailedUnknownError(
        textId = R.string.AnalyticsEventAction_JoinConferenceFailedUnknownError,
        category = AnalyticsEventCategory.JoinConference,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_JoinConferenceFailedUnknownError,
    ),
    ConferenceEndLeft(
        textId = R.string.AnalyticsEventAction_ConferenceEndLeft,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_ConferenceEndLeft,
    ),
    ConferenceEndBooted(
        textId = R.string.AnalyticsEventAction_ConferenceEndBooted,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_ConferenceEndBooted,
    ),
    ConferenceEndSignalingConnectionLost(
        textId = R.string.AnalyticsEventAction_ConferenceEndSignalingConnectionLost,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_ConferenceEndSignalingConnectionLost,
    ),
    ConferenceEndMediaConnectionLost(
        textId = R.string.AnalyticsEventAction_ConferenceEndMediaConnectionLost,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_ConferenceEndMediaConnectionLost,
    ),
    ConferenceEndUnknownError(
        textId = R.string.AnalyticsEventAction_ConferenceEndUnknownError,
        category = AnalyticsEventCategory.ConferenceEnd,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_ConferenceEndUnknownError,
    ),
    InCallCodecVideoH264(
        textId = R.string.AnalyticsEventAction_InCallCodecVideoH264,
        category = AnalyticsEventCategory.InCallCodec,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_InCallCodecVideoH264,
    ),
    InCallCodecVideoH264SVC(
        textId = R.string.AnalyticsEventAction_InCallCodecVideoH264SVC,
        category = AnalyticsEventCategory.InCallCodec,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_InCallCodecVideoH264SVC,
    ),
    InCallCodecAudioSPEEXRED(
        textId = R.string.AnalyticsEventAction_InCallCodecAudioSPEEXRED,
        category = AnalyticsEventCategory.InCallCodec,
        jniValue = ConnectorAnalyticsEventAction.VIDYO_CONNECTORANALYTICSEVENTACTION_InCallCodecAudioSPEEXRED,
    );

    companion object {
        fun fromName(name: String): AnalyticsEventAction {
            return values().find { it.name == name } ?: Unknown
        }

        fun fromJniValue(jniValue: ConnectorAnalyticsEventAction): AnalyticsEventAction {
            return values().find { it.jniValue == jniValue } ?: Unknown
        }
    }
}
