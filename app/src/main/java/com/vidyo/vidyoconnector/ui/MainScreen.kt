package com.vidyo.vidyoconnector.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.vidyo.vidyoconnector.bl.ProtocolHandler
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceState
import com.vidyo.vidyoconnector.ui.utils.*
import com.vidyo.vidyoconnector.ui.utils.styles.VcColors
import dev.matrix.compose_routes.NavRoutes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@Composable
fun MainScreen(activity: Activity) {
    MaterialTheme(colors = VcColors.colors) {
        PermissionsGuard(activity) {
            Content(activity)
        }
    }
}

@Composable
private fun Content(activity: Activity) {
    ConnectorManager.LifecycleTracker()

    LaunchedEffect("screen_on") {
        val state = ConnectorManager.conference.state
        state.map { it.isActive }.distinctUntilChanged().collect {
            when (it) {
                true -> activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else -> activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    Navigation(activity = activity)
}

@Composable
private fun Navigation(activity: Activity) {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalActivity provides activity,
        LocalNavController provides navController,
    ) {
        NavHost(navController = navController, startDestination = NavRoutes.JoinAsGuestScreen()) {
            NavRoutes.registerAll(this)
        }
    }

    val conference = LocalConnectorManager.current.conference
    LaunchedEffect("conference") {
        val conferenceActive = conference.state
            .map { it.isActive && it != ConferenceState.Joining }
            .distinctUntilChanged()

        conferenceActive.collect {
            val route = when (it) {
                true -> NavRoutes.ConferenceScreen()
                else -> NavRoutes.JoinAsGuestScreen()
            }

            val firstEntry = navController.backQueue.firstOrNull { entry ->
                !entry.destination.route.isNullOrEmpty()
            }

            if (firstEntry == null || firstEntry.destination.route != route) {
                navController.navigate(route) { clearBackStack(navController) }
            }
        }
    }
    LaunchedEffect("protocol_handler") {
        ProtocolHandler.track().filterNotNull().collect {
            if (conference.state.value.isActive) {
                return@collect
            }
            navController.navigate(NavRoutes.JoinAsGuestScreen()) { clearBackStack(navController) }
        }
    }
}
