package com.vidyo.vidyoconnector.ui.utils

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavBackIcon(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    if (navController.backQueue.isNotEmpty()) {
        NavBackIcon(modifier = modifier) {
            navController.popBackStack()
        }
    }
}

@Composable
fun NavBackIcon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "back",
            tint = MaterialTheme.colors.onBackground,
        )
    }
}
