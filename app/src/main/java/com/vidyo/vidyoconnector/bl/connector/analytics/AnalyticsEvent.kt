package com.vidyo.vidyoconnector.bl.connector.analytics

import com.vidyo.VidyoClient.Connector.Connector.ConnectorAnalyticsEventAction
import com.vidyo.VidyoClient.Connector.Connector.ConnectorAnalyticsEventCategory

data class AnalyticsEvent(
    val category: ConnectorAnalyticsEventCategory,
    val action: ConnectorAnalyticsEventAction,
) {
}
