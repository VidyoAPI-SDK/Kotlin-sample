package com.vidyo.vidyoconnector.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun collectLifecycleAsState(): State<Lifecycle.State> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleState = remember { mutableStateOf(Lifecycle.State.INITIALIZED) }

    DisposableEffect("lifecycle") {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState.value = event.targetState
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return lifecycleState
}
