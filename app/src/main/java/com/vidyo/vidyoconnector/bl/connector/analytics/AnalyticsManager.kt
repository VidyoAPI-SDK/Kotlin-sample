package com.vidyo.vidyoconnector.bl.connector.analytics

import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesManager
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesProperty
import com.vidyo.vidyoconnector.ui.utils.showToast
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AnalyticsManager(
    private val scope: ConnectorScope,
    private val preferences: PreferencesManager,
) {
    data class EventActionInfo(
        val action: AnalyticsEventAction,
        val preference: PreferencesProperty<Boolean>,
    )

    val enabled = preferences.createPreferencesProperty(
        key = "analytics_enabled",
        read = { getBoolean(it, true) },
        write = { key, value -> putBoolean(key, value) },
    )

    val googleEnabled = MutableStateFlow(scope.connector.isGoogleAnalyticsServiceEnabled)
    val googleTrackingId = MutableStateFlow(scope.connector.googleAnalyticsServiceID.orEmpty())
    val insightEnabled = MutableStateFlow(scope.connector.isInsightsServiceEnabled)
    val insightAddress = MutableStateFlow(scope.connector.insightsServiceUrl.orEmpty())

    val events = createAnalyticsEventActionPreferences()

    init {
        combine(googleEnabled, googleTrackingId, ::Pair).collectInScope(scope) {
            scope.connector.stopGoogleAnalyticsService()
            if (it.first) {
                scope.connector.startGoogleAnalyticsService(it.second)
            }
        }

        combine(insightEnabled, insightAddress, ::Pair).collectInScope(scope) {
            scope.connector.stopInsightsService()
            if (it.first) {
                if (it.second.isEmpty()) {
                    insightEnabled.value = false
                    showToast(R.string.analytics_insight_error)
                } else {
                    scope.connector.startInsightsService(it.second)
                }
            }
        }
    }

    private fun createAnalyticsEventActionPreferences(): Map<AnalyticsEventCategory, List<EventActionInfo>> {
        val enabled = HashSet<AnalyticsEventAction>()
        scope.connector.getGoogleAnalyticsEventTable { events ->
            events.asSequence()
                .filter { it.enable }
                .map { AnalyticsEventAction.fromJniValue(it.eventAction) }
                .filter { it.category != AnalyticsEventCategory.None }
                .toCollection(enabled)
        }

        val map = LinkedHashMap<AnalyticsEventCategory, ArrayList<EventActionInfo>>(AnalyticsEventAction.values().size)
        for (action in AnalyticsEventAction.values()) {
            if (action.category == AnalyticsEventCategory.None) {
                continue
            }

            val preference = preferences.createConnectorProperty(
                key = "analytics_event_${action.name}",
                read = { getBoolean(it, enabled.contains(action)) },
                write = { key, value -> putBoolean(key, value) },
                set = {
                    scope.connector.googleAnalyticsControlEventAction(
                        action.category.jniValue,
                        action.jniValue,
                        it,
                    )
                },
            )

            map.getOrPut(action.category) { ArrayList() }.add(EventActionInfo(action, preference))
        }
        return map
    }
}
