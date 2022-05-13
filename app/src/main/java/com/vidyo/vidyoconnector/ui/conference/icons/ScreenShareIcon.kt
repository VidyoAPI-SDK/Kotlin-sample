package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.local.screen_share.source.DeviceScreenShareSource
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoFrameRate
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun ScreenShareIcon(modifier: Modifier = Modifier) {
    val manager = LocalConnectorManager.current.media.localScreenShare
    val active = manager.active.collectAsState().value

    val image = when (active) {
        true -> R.drawable.ic_screen_share_on
        else -> R.drawable.ic_screen_share_off
    }

    val context = LocalContext.current
    val source = remember { DeviceScreenShareSource(context) }

    val stopShareDialog = rememberSaveable { mutableStateOf(false) }
    val frameRateDialog = rememberSaveable { mutableStateOf(false) }
    val frameRateType = rememberSaveable { mutableStateOf(VirtualVideoFrameRate.Default) }

    val result = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val flow = source.handleScreenCaptureResult(it)
            if (flow != null) {
                manager.start(flow, frameRateType.value)
            }
        },
    )

    IconButton(
        onClick = {
            when (active) {
                true -> stopShareDialog.value = true
                else -> frameRateDialog.value = true
            }
        },
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = if (active) "screen share active" else "screen share inactive",
        )
    }

    if (frameRateDialog.value) {
        SelectFrameRateDialog {
            if (it != null) {
                frameRateType.value = it
                result.launch(source.createScreenCaptureIntent())
            }
            frameRateDialog.value = false
        }
    }

    if (stopShareDialog.value) {
        StopScreenShareDialog {
            if (it) {
                manager.stop()
            }
            stopShareDialog.value = false
        }
    }
}

@Composable
private fun SelectFrameRateDialog(onSubmit: (VirtualVideoFrameRate?) -> Unit) {
    val frameRate = remember { mutableStateOf(VirtualVideoFrameRate.Default) }

    AlertDialog(
        onDismissRequest = { onSubmit(null) },
        title = {
            Text(
                text = stringResource(R.string.selectFrameRateDialog_title),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.selectFrameRateDialog_message))

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { frameRate.value = VirtualVideoFrameRate.Normal },
                ) {
                    RadioButton(
                        selected = frameRate.value == VirtualVideoFrameRate.Normal,
                        onClick = { frameRate.value = VirtualVideoFrameRate.Normal })
                    Text(
                        text = stringResource(R.string.selectFrameRateDialog_normal),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { frameRate.value = VirtualVideoFrameRate.High },
                ) {
                    RadioButton(
                        selected = frameRate.value == VirtualVideoFrameRate.High,
                        onClick = { frameRate.value = VirtualVideoFrameRate.High })
                    Text(
                        text = stringResource(R.string.selectFrameRateDialog_high),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onSubmit(null) }) {
                val text = stringResource(R.string.selectFrameRateDialog_negative)
                Text(text = remember { text.uppercase() })
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(frameRate.value) }) {
                val text = stringResource(R.string.selectFrameRateDialog_positive)
                Text(text = remember { text.uppercase() })
            }
        },
    )
}

@Composable
private fun StopScreenShareDialog(onSubmit: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onSubmit(false) },
        title = { Text(text = stringResource(R.string.stopScreenShareDialog_title)) },
        text = { Text(text = stringResource(R.string.stopScreenShareDialog_message)) },
        dismissButton = {
            TextButton(onClick = { onSubmit(false) }) {
                val text = stringResource(R.string.stopScreenShareDialog_negative)
                Text(text = remember { text.uppercase() })
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(true) }) {
                val text = stringResource(R.string.stopScreenShareDialog_positive)
                Text(text = remember { text.uppercase() })
            }
        },
    )
}
