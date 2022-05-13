package com.vidyo.vidyoconnector.ui.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.vidyo.vidyoconnector.R

private enum class PermissionState {
    Granted, NotGranted, Rejected
}

private val basicPermissions = listOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.READ_PHONE_STATE,
)

@Composable
fun PermissionsGuard(activity: Activity, content: @Composable () -> Unit) {
    // TODO replace when stable - https://google.github.io/accompanist/permissions/

    val context = LocalContext.current
    val preferences = remember {
        activity.getSharedPreferences("permissions", Context.MODE_PRIVATE)
    }

    fun checkPermissions() = basicPermissions.maxOf {
        if (context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED) {
            return@maxOf PermissionState.Granted
        }
        if (preferences.contains(it) && !activity.shouldShowRequestPermissionRationale(it)) {
            return@maxOf PermissionState.Rejected
        }
        return@maxOf PermissionState.NotGranted
    }

    // TODO simplify (early return) when fixed - https://issuetracker.google.com/issues/205344323
    val state = remember { mutableStateOf(checkPermissions()) }
    if (state.value == PermissionState.Granted) {
        content()
    } else {
        if (state.value == PermissionState.Rejected) {
            GoToSettings(activity)
        }

        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(Unit) {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) = when (checkPermissions()) {
                    PermissionState.Granted -> {
                        state.value = PermissionState.Granted
                    }
                    PermissionState.Rejected -> {
                        state.value = PermissionState.Rejected
                    }
                    PermissionState.NotGranted -> {
                        preferences.edit {
                            basicPermissions.forEach { putBoolean(it, true) }
                        }
                        activity.requestPermissions(basicPermissions.toTypedArray(), 0)
                    }
                }
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
    }
}

@Composable
private fun GoToSettings(activity: Activity) {
    fun openSettings() = activity.startActivity(
        Intent()
            .setData(Uri.fromParts("package", activity.packageName, null))
            .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    )

    AlertDialog(
        title = { Text(text = stringResource(R.string.permission_dialog_title)) },
        text = { Text(text = stringResource(R.string.permission_dialog_message)) },
        confirmButton = {
            TextButton(onClick = { openSettings() }) {
                Text(text = stringResource(R.string.permission_dialog_go_to_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = { activity.finish() }) {
                Text(text = stringResource(R.string.permission_dialog_exit))
            }
        },
        onDismissRequest = { activity.finish() },
    )
}
