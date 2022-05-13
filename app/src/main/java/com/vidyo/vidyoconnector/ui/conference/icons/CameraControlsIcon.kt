package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.base.Camera
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import kotlinx.coroutines.flow.*

@Composable
fun CameraControlsIcon(state: MutableState<Camera?>, modifier: Modifier = Modifier) {
    val media = LocalConnectorManager.current.media
    val all = remember { mutableStateOf(emptyList<Camera>()) }

    LaunchedEffect("controlCapabilities") {
        val localFlow = media.localCamera.selected.transformLatest { camera ->
            camera ?: return@transformLatest emit(null)
            camera.controlCapabilities.collect { capabilities ->
                emit(camera.takeIf { capabilities.hasAny })
            }
        }

        val remoteFlow = media.remoteCamera.all.flatMapLatest { list ->
            val flows = list.map { camera ->
                camera.controlCapabilities.map { capabilities ->
                    camera.takeIf { capabilities.hasAny }
                }
            }
            combine(flows) { it }
        }

        combine(localFlow, remoteFlow) { local, remote ->
            val list = ArrayList<Camera>(remote.size + 1)
            local?.let { list.add(it) }
            remote.filterNotNullTo(list)
            all.value = list
        }.collect()
    }

    when (all.value.isNotEmpty()) {
        true -> Content(state, all.value, modifier)
        else -> state.value = null
    }
}

@Composable
private fun Content(
    state: MutableState<Camera?>,
    cameras: List<Camera>,
    modifier: Modifier = Modifier,
) {
    val dialog = rememberSaveable { mutableStateOf(false) }

    val image = when (state.value != null) {
        true -> R.drawable.ic_fecc_on
        else -> R.drawable.ic_fecc_off
    }

    IconButton(
        onClick = {
            when {
                state.value != null -> {
                    state.value = null
                }
                cameras.size == 1 -> {
                    state.value = cameras.first()
                }
                cameras.size > 1 -> {
                    dialog.value = true
                }
            }
        },
        enabled = cameras.isNotEmpty(),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(image),
            contentDescription = "camera PTZ controls",
            tint = Color.White.copy(alpha = LocalContentAlpha.current),
        )
    }

    if (dialog.value) {
        when (cameras.size > 1) {
            true -> Dialog(cameras) {
                state.value = it
                dialog.value = false
            }
            else -> dialog.value = false
        }
    }
}

@Composable
private fun Dialog(cameras: List<Camera>, onResult: (Camera?) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult(null) },
        title = { Text(text = stringResource(R.string.cameraControlsIcon_title)) },
        buttons = {
            Column(modifier = Modifier.fillMaxWidth()) {
                DialogContent(
                    cameras = cameras,
                    onResult = onResult,
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                ) {
                    TextButton(onClick = { onResult(null) }) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                }
            }
        },
    )
}

@Composable
private fun DialogContent(
    cameras: List<Camera>,
    onResult: (Camera?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val participants = LocalConnectorManager.current.participants

    LazyColumn(modifier = modifier) {
        items(cameras.size) {
            val item = cameras[it]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .clickable { onResult(item) }
                    .padding(vertical = 8.dp),
            ) {
                val text = remember { mutableStateOf("") }

                if (item.participantId.isNotEmpty()) {
                    LaunchedEffect(item.participantId) {
                        val participant = participants.findParticipant(item.participantId)
                        if (participant != null) {
                            text.value = participant.name
                        }
                    }
                } else {
                    text.value = stringResource(R.string.cameraControlsIcon_localCamera, item.name)
                }

                RadioButton(selected = false, onClick = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text.value)
            }
        }
    }
}
