package com.vidyo.vidyoconnector.ui.conference.participants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.remote.camera.collectRemoteCameraByParticipant
import com.vidyo.vidyoconnector.bl.connector.participants.Participant
import com.vidyo.vidyoconnector.bl.connector.participants.ParticipantClearance
import com.vidyo.vidyoconnector.bl.connector.participants.ParticipantTrust
import com.vidyo.vidyoconnector.ui.conference.chats.ParticipantIcon
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun ParticipantItem(modifier: Modifier = Modifier, participant: Participant) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        ParticipantIcon(
            participant = participant,
            modifier = Modifier.size(50.dp),
            active = participant.clearance == ParticipantClearance.Owner,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = participant.name,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                CameraPrivacy(participant = participant)
                MicrophonePrivacy(participant = participant)
                Tags(participant = participant)
            }
        }

        CameraPresets(participant = participant)
        Pin(participant = participant)
    }
}

@Composable
private fun Pin(participant: Participant, modifier: Modifier = Modifier) {
    if (participant.isLocal) {
        return
    }

    val manager = LocalConnectorManager.current

    val remoteCamera = manager.media.remoteCamera.trackByParticipantId(participant.id).collectAsState(true).value
    val participants = manager.participants
    val pinned = participants.pinned.collectAsState().value == participant.id

    val tint = when (pinned) {
        true -> ColorFilter.tint(Color.Red)
        else -> null
    }

    AnimatedVisibility(
        visible = remoteCamera != null,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        IconButton(
            onClick = { participants.setParticipantPinned(participant, !pinned) },
            modifier = modifier.size(50.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_pin),
                contentDescription = "pinned $pinned",
                colorFilter = tint,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun CameraPresets(participant: Participant, modifier: Modifier = Modifier) {
    if (participant.isLocal) {
        return
    }

    val camera = collectRemoteCameraByParticipant(participant).value
    if (camera == null || camera.id.isEmpty()) {
        return
    }

    val presets = camera.presets.collectAsState(emptyList()).value
    if (presets.isEmpty()) {
        return
    }

    val dialog = rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { dialog.value = true },
        modifier = modifier.size(50.dp),
    ) {
        Image(
            painter = rememberVectorPainter(Icons.Default.ControlCamera),
            contentDescription = "camera presets",
            colorFilter = ColorFilter.tint(color = Color.White),
            modifier = Modifier.size(22.dp),
        )
    }

    if (dialog.value) {
        RemoteCameraPresetDialog(camera) { dialog.value = false }
    }
}

@Composable
private fun CameraPrivacy(participant: Participant, modifier: Modifier = Modifier) {
    val media = LocalConnectorManager.current.media
    val available = when (participant.isLocal) {
        true -> !media.localCamera.muted.collectAsState().value.muted
        else -> media.remoteCamera.trackByParticipantId(participant.id).collectAsState(true).value != null
    }

    if (!available) {
        Image(
            painter = painterResource(R.drawable.ic_camera_off),
            contentDescription = "camera muted",
            modifier = modifier.size(16.dp),
        )
    }
}

@Composable
private fun MicrophonePrivacy(participant: Participant, modifier: Modifier = Modifier) {
    val media = LocalConnectorManager.current.media
    val available = when (participant.isLocal) {
        true -> !media.localMicrophone.muted.collectAsState().value.muted
        else -> media.remoteMicrophone.trackParticipantAvailability(participant.id)
            .collectAsState(true)
            .value
    }

    if (!available) {
        Image(
            painter = painterResource(R.drawable.ic_microphone_off),
            contentDescription = "microphone muted",
            modifier = modifier.size(16.dp),
        )
    }
}

@Composable
private fun Tags(participant: Participant, modifier: Modifier = Modifier) {
    val trust = stringResource(participant.trust.textId)
    val clearance = stringResource(participant.clearance.textId)

    val text = remember(participant) {
        buildString {
            fun smartAppend(string: String) {
                if (isNotEmpty()) {
                    append(", ")
                }
                append(string)
            }
            if (participant.trust == ParticipantTrust.Anonymous) {
                smartAppend(trust)
            }
            if (participant.clearance !in arrayOf(ParticipantClearance.None, ParticipantClearance.Member)) {
                smartAppend(clearance)
            }
        }
    }

    Text(
        text = text,
        fontSize = 10.sp,
        modifier = modifier,
    )
}
