package com.vidyo.vidyoconnector.ui.conference.participants

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.remote.camera.RemoteCamera
import com.vidyo.vidyoconnector.ui.utils.dialog.ListAlertDialog

@Composable
fun RemoteCameraPresetDialog(camera: RemoteCamera, onDismissRequest: () -> Unit) {
    val presets = camera.presets.collectAsState(emptyList()).value
    if (presets.isEmpty()) {
        return
    }

    ListAlertDialog(
        onDismissRequest = onDismissRequest,
        onItemSelected = {
            camera.handle.activatePreset(presets[it].index)
            onDismissRequest()
        },
        title = {
            Text(
                text = stringResource(R.string.remoteCameraPresetDialog_title),
                textAlign = TextAlign.Center,
            )
        },
        count = presets.size,
        item = {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = presets[it].name)
        },
    )
}
