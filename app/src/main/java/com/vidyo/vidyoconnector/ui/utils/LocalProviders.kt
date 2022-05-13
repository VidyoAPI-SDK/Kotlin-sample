package com.vidyo.vidyoconnector.ui.utils

import android.app.Activity
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager

val LocalActivity = compositionLocalOf<Activity> {
    error("Activity not present")
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("NavController not present")
}

val LocalConnectorManager = staticCompositionLocalOf {
    ConnectorManager
}
