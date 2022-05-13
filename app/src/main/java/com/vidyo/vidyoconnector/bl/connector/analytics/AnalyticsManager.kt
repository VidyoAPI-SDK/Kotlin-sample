package com.vidyo.vidyoconnector.bl.connector.analytics

import android.content.SharedPreferences
import com.vidyo.vidyoconnector.BuildConfig
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesManager
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesProperty
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import kotlinx.coroutines.flow.*
import org.json.JSONObject

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
        read = { getBoolean(it, false) },
        write = { key, value -> putBoolean(key, value) },
    )

    val type = preferences.createPreferencesProperty(
        key = "analytics_type",
        read = { AnalyticsType.fromOrdinal(getInt(it, -1)) { AnalyticsType.Google } },
        write = { key, value -> putInt(key, value.ordinal) },
    )

    val google = preferences.createPreferencesProperty(
        key = "analytics_Google",
        read = { AnalyticsInfo.Google(getJson(it)) },
        write = { key, value -> putString(key, value.toJson()) },
    )

    val vidyoInsight = preferences.createPreferencesProperty(
        key = "analytics_VidyoInsight",
        read = { AnalyticsInfo.VidyoInsight(getJson(it)) },
        write = { key, value -> putString(key, value.toJson()) },
    )

    val events = createAnalyticsEventActionPreferences()

    init {
        combine(type, enabled, ::Pair)
            .flatMapLatest {
                if (!it.second) {
                    return@flatMapLatest flowOf(AnalyticsInfo.None)
                }
                when (it.first) {
                    AnalyticsType.None -> flowOf(AnalyticsInfo.None)
                    AnalyticsType.Google -> google
                    AnalyticsType.VidyoInsight -> vidyoInsight
                }
            }
            .distinctUntilChanged()
            .collectInScope(scope) {
                scope.connector.analyticsStop()
                when (it) {
                    is AnalyticsInfo.None -> Unit
                    is AnalyticsInfo.Google -> {
                        val trackingId = it.trackingId.ifEmpty { BuildConfig.DEFAULT_GOOGLE_ANALYTICS_ID }
                        scope.connector.analyticsStart(it.type.jniValue, "", trackingId)
                    }
                    is AnalyticsInfo.VidyoInsight -> {
                        scope.connector.analyticsStart(it.type.jniValue, it.serverUrl, "")
                    }
                }
            }
    }

    private fun SharedPreferences.getJson(key: String): JSONObject {
        return try {
            JSONObject(getString(key, "").orEmpty())
        } catch (e: Exception) {
            JSONObject()
        }
    }

    private fun createAnalyticsEventActionPreferences(): List<EventActionInfo> {
        val enabled = HashSet<AnalyticsEventAction>()
        scope.connector.getAnalyticsEventTable { events ->
            events.asSequence()
                .filter { it.enable }
                .map { AnalyticsEventAction.fromJniValue(it.eventAction) }
                .filter { it.category != AnalyticsEventCategory.None }
                .toCollection(enabled)
        }

        val list = ArrayList<EventActionInfo>(AnalyticsEventAction.values().size)
        for (action in AnalyticsEventAction.values()) {
            if (action.category == AnalyticsEventCategory.None) {
                continue
            }

            val preference = preferences.createConnectorProperty(
                key = "analytics_event_${action.name}",
                read = { getBoolean(it, enabled.contains(action)) },
                write = { key, value -> putBoolean(key, value) },
                set = {
                    scope.connector.analyticsControlEventAction(
                        action.category.jniValue,
                        action.jniValue,
                        it
                    )
                },
            )

            list.add(EventActionInfo(action, preference))
        }
        return list
    }
}
