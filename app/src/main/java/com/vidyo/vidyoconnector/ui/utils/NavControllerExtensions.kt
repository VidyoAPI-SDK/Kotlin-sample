package com.vidyo.vidyoconnector.ui.utils

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavOptionsBuilder.clearBackStack(navController: NavController) {
    val record = navController.backQueue.firstOrNull { it.destination.route != null }
    if (record != null) {
        popUpTo(record.destination.route.orEmpty()) {
            inclusive = true
        }
    }
}
