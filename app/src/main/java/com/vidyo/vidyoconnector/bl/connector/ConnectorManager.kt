package com.vidyo.vidyoconnector.bl.connector

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Connector.ConnectorPkg
import com.vidyo.vidyoconnector.BuildConfig
import com.vidyo.vidyoconnector.appContext
import com.vidyo.vidyoconnector.bl.connector.analytics.AnalyticsManager
import com.vidyo.vidyoconnector.bl.connector.chats.ChatsManager
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import com.vidyo.vidyoconnector.bl.connector.logs.LogsManager
import com.vidyo.vidyoconnector.bl.connector.media.MediaManager
import com.vidyo.vidyoconnector.bl.connector.network.NetworksManager
import com.vidyo.vidyoconnector.bl.connector.participants.ParticipantsManager
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesManager
import com.vidyo.vidyoconnector.bl.connector.virtual_background.VirtualBackgroundManager
import com.vidyo.vidyoconnector.utils.collectLifecycleAsState
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.json.JSONObject
import java.io.File

@SuppressLint("StaticFieldLeak")
object ConnectorManager {
    private const val LOG_TAG = "ConnectorManager"
    private const val DEFAULT_PARTICIPANT_COUNT = 3
    private const val PIP_PARTICIPANTS_COUNT = 1

    val rootFolder = File(appContext.filesDir, "vidyo")

    private val defaultViewStyle = Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default
    val layout = ConnectorLayout(appContext)
    private val scope = createScope(layout.context)
    private val appResumedTokens = ArrayList<Any>()
    private val layoutRequestQueue = ArrayList<(View) -> Unit>()

    val version = scope.connector.version.orEmpty()
    val numberOfParticipants = MutableStateFlow(DEFAULT_PARTICIPANT_COUNT)

    val preferences = PreferencesManager(scope)
    val logs = LogsManager(scope, preferences)
    val analytics = AnalyticsManager(scope, preferences)
    val networks = NetworksManager(scope)
    val conference = ConferenceManager(scope)
    val media = MediaManager(scope, conference)
    val participants = ParticipantsManager(scope)
    val chats = ChatsManager(scope, media, participants, conference)
    val virtualBackground = VirtualBackgroundManager(scope)

    private val _pipActiveState = MutableStateFlow(false)
    val pipActiveState = _pipActiveState.asStateFlow()

    init {
        numberOfParticipants.collectInScope(scope) {
            Log.d(LOG_TAG, "assignViewToCompositeRenderer numberOfParticipants: $it")
            scope.connector.assignViewToCompositeRenderer(layout, defaultViewStyle, it)
        }
    }

    /**
     *
     * @param isPipActive Boolean
     */
    fun updatePipActiveState(isPipActive: Boolean){
        Log.d(LOG_TAG, "updatePipActiveState, isPipActive: $isPipActive")
        _pipActiveState.value = isPipActive
        // To hide self preview in PIP mode since PIP window is small in size
        scope.connector.showPreview(!isPipActive)
        // To show only one participant in PIP mode since PIP window is small in size
        numberOfParticipants.value = if(isPipActive) {
            PIP_PARTICIPANTS_COUNT
        }else{
            DEFAULT_PARTICIPANT_COUNT
        }
        if(pipActiveState.value) {
            // For PIP mode, VIDYO_CONNECTORMODE_Foreground will work.
            scope.connector.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Foreground)
        }
    }

    private fun createScope(context: Context): ConnectorScope {
        ConnectorPkg.setApplicationUIContext(context)
        if (!ConnectorPkg.initialize()) {
            throw Exception("ConnectorPkg.initialize() failed")
        }

        val googleAnalyticsId = BuildConfig.DEFAULT_GOOGLE_ANALYTICS_ID
        val googleAnalyticsKey = BuildConfig.DEFAULT_GOOGLE_ANALYTICS_KEY
        if (googleAnalyticsId.isNotEmpty() && googleAnalyticsKey.isNotEmpty()) {
            val json = buildJsonObject {
                putJsonObject("GoogleAnalyticsData") {
                    put("id", googleAnalyticsId)
                    put("key", googleAnalyticsKey)
                }
            }
            if (!ConnectorPkg.setExperimentalOptions(json.toString())) {
                throw Exception("ConnectorPkg.setExperimentalOptions() failed")
            }
        }

        LogsManager.logsFile.parentFile?.mkdirs()
        val connector = Connector(
            null,
            defaultViewStyle,
            DEFAULT_PARTICIPANT_COUNT,
            "",
            LogsManager.logsFile.absolutePath,
            0,
        )

        val vidyoInsightsUrl = BuildConfig.DEFAULT_INSIGHTS_URL
        val vidyoInsightsEnabled = BuildConfig.DEFAULT_INSIGHTS_ENABLED

        if (vidyoInsightsEnabled.toBoolean() == true
            && vidyoInsightsUrl.isNotEmpty()) {
            connector.startInsightsService(vidyoInsightsUrl)
        }

        return ConnectorScope(context, connector)
    }

    private fun addAppResumedToken(token: Any) {
        if (appResumedTokens.add(token) && appResumedTokens.size == 1) {
            scope.connector.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Foreground)
        }
    }

    private fun removeAppResumedToken(token: Any) {
        if (appResumedTokens.remove(token) && appResumedTokens.isEmpty()) {
            scope.connector.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Background)
        }
    }

    private fun lockLayout(parent: ViewGroup): Flow<View?> = channelFlow {
        val callback: (View?) -> Unit = { trySend(it) }

        (layout.parent as? ViewGroup)?.removeAllViews()
        parent.addView(layout)

        layoutRequestQueue.add(callback)
        callback(layout)

        awaitClose {
            val index = layoutRequestQueue.indexOf(callback)
            layoutRequestQueue.removeAt(index)
            if (index == layoutRequestQueue.size) {
                parent.removeAllViews()
                layoutRequestQueue.lastOrNull()?.invoke(layout)
            }
            if (layoutRequestQueue.isEmpty()) {
                scope.connector.hideView(layout)
            }
        }
    }

    // region Composable Widgets

    @Composable
    fun LifecycleTracker() {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect("lifecycle") {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) = addAppResumedToken(this)
                override fun onPause(owner: LifecycleOwner) = removeAppResumedToken(this)
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
    }

    @Composable
    fun PreviewView(modifier: Modifier = Modifier) {
        val lifecycle = collectLifecycleAsState().value
        val camera = media.localCamera.selected.collectAsState().value
        if (camera != null && lifecycle.isAtLeast(Lifecycle.State.RESUMED)) {
            ConferenceView(
                modifier = modifier,
                showViewLabel = false,
                showAudioMeters = false,
                allowRemoteOfParticipants = false,
            )
        } else {
            Box(modifier = modifier)
        }
    }

    @Composable
    fun ConferenceView(
        modifier: Modifier = Modifier,
        showViewLabel: Boolean = true,
        showAudioMeters: Boolean = true,
        allowRemoteOfParticipants: Boolean = true,
    ) {
        val context = LocalContext.current
        val wrapper = remember { ConnectorLayout(context) }

        LaunchedEffect("view") {
            lockLayout(wrapper).collectLatest { view ->
                val number = when (allowRemoteOfParticipants) {
                    true -> numberOfParticipants.value
                    else -> 0
                }

                val rendererOptions = JSONObject().apply {
                    put("SetPixelDensity", context.resources.displayMetrics.densityDpi)
                    put("ViewingDistance", 1.0)
                }

                scope.connector.assignViewToCompositeRenderer(view, defaultViewStyle, number)
                scope.connector.showViewLabel(view, showViewLabel)
                scope.connector.showAudioMeters(view, showAudioMeters)
                scope.connector.setRendererOptionsForViewId(view, rendererOptions.toString())

                if (allowRemoteOfParticipants) {
                    numberOfParticipants.collect {
                        scope.connector.assignViewToCompositeRenderer(view, defaultViewStyle, it)
                    }
                }
            }
        }

        AndroidView(modifier = modifier, factory = { wrapper })
    }

    // endregion
}
