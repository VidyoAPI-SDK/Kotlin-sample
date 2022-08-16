package com.vidyo.vidyoconnector.bl.connector

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
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
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import org.json.JSONObject
import java.io.File
import java.util.concurrent.atomic.AtomicReference

@SuppressLint("StaticFieldLeak")
object ConnectorManager {
    val rootFolder = File(appContext.filesDir, "vidyo")

    private val defaultViewStyle = Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default
    private val layout = ConnectorLayout(appContext)
    private val scope = createScope(layout.context)
    private val appResumedTokens = ArrayList<Any>()
    private val layoutRequestQueue = ArrayList<(View) -> Unit>()

    val version = scope.connector.version.orEmpty()
    val preferences = PreferencesManager(scope)
    val logs = LogsManager(scope, preferences)
    val analytics = AnalyticsManager(scope, preferences)
    val networks = NetworksManager(scope)
    val conference = ConferenceManager(scope)
    val media = MediaManager(scope, conference, preferences)
    val participants = ParticipantsManager(scope)
    val chats = ChatsManager(scope, media, participants, conference)
    val virtualBackground = VirtualBackgroundManager(scope)

    init {
        preferences.numberOfParticipants.collectInScope(scope) {
            scope.connector.assignViewToCompositeRenderer(layout, defaultViewStyle, it)
        }
    }

    private fun createScope(context: Context): ConnectorScope {
        ConnectorPkg.setApplicationUIContext(context)
        if (!ConnectorPkg.initialize()) {
            throw Exception("ConnectorPkg.initialize() failed")
        }
        if (BuildConfig.DEFAULT_GOOGLE_ANALYTICS_ID.isNotEmpty()) {
            val json = "{\"googleAnalyticsDefaultId\":\"${BuildConfig.DEFAULT_GOOGLE_ANALYTICS_ID}\"}"
            if (!ConnectorPkg.setExperimentalOptions(json)) {
                throw Exception("ConnectorPkg.setExperimentalOptions() failed")
            }
        }

        LogsManager.logsFile.parentFile?.mkdirs()
        val connector = Connector(
            null,
            defaultViewStyle,
            3,
            "",
            LogsManager.logsFile.absolutePath,
            0,
        )
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

    private fun lockLayout(block: (View) -> Unit) {
        if (layoutRequestQueue.add(block) && layoutRequestQueue.size == 1) {
            block(layout)
        }
    }

    private fun unlockLayout(block: (View) -> Unit) {
        val index = layoutRequestQueue.indexOf(block)
        layoutRequestQueue.removeAt(index)
        if (index == 0) {
            layoutRequestQueue.getOrNull(0)?.invoke(layout)
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
        val view = remember { AtomicReference<View>() }
        val camera = media.localCamera.selected.collectAsState().value

        AndroidView(
            modifier = modifier,
            factory = { ConnectorLayout(it).also { v -> view.set(v) } },
            update = {
                if (it.tag == camera) {
                    return@AndroidView
                }
                if (camera != null) {
                    scope.connector.assignViewToCompositeRenderer(it, defaultViewStyle, 0)
                    scope.connector.showViewLabel(it, false)
                    scope.connector.showAudioMeters(it, false)
                } else {
                    scope.connector.hideView(it)
                }
                it.tag = camera
            }
        )

        DisposableEffect("hide") {
            onDispose { view.get()?.also { scope.connector.hideView(it) } }
        }
    }

    @Composable
    fun ConferenceView(modifier: Modifier = Modifier) {
        val layout = remember { mutableStateOf<View?>(null) }
        DisposableEffect("lock") {
            val callback: (View) -> Unit = {
                scope.connector.assignViewToCompositeRenderer(
                    it,
                    defaultViewStyle,
                    preferences.numberOfParticipants.value,
                )
                val json = JSONObject().apply {
                    put("SetPixelDensity", appContext.resources.displayMetrics.densityDpi)
                    put("ViewingDistance", 1.0)
                }
                scope.connector.setRendererOptionsForViewId(it, json.toString())
                layout.value = it
            }
            lockLayout(callback)
            onDispose { unlockLayout(callback) }
        }

        val view = layout.value
        if (view != null) {
            AndroidView(modifier = modifier, factory = { view })
        }
    }

    // endregion
}
