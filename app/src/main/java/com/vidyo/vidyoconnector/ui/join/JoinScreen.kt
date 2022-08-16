package com.vidyo.vidyoconnector.ui.join

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.bl.ProtocolHandler
import com.vidyo.vidyoconnector.ui.conference.icons.*
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.ProgressDialog
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun JoinScreen() {
    val manager = LocalConnectorManager.current
    val model = rememberSaveable(saver = JoinViewModel.Saver) { JoinViewModel() }

    val conference = manager.conference.conference.collectAsState()
    if (conference.value.state.isActive) {
        ProgressDialog()
    }

    LaunchedEffect("init") {
        val args = ProtocolHandler.consume()
        if (args == null || conference.value.state.isActive) {
            if (model.type.value == JoinContent.Unknown) {
                model.type.value = JoinContent.Auto
            }
            return@LaunchedEffect
        }

        model.type.value = JoinContent.Guest
        model.portal.value = args.portal
        model.username.value = args.name
        model.roomKey.value = args.roomKey
        model.roomPin.value = args.roomPin

        manager.media.localCamera.requestMutedState(args.muteCamera)
        manager.media.localSpeaker.requestMutedState(args.muteSpeaker)
        manager.media.localMicrophone.requestMutedState(args.muteMicrophone)

        if (args.autoJoin) {
            model.join(manager.conference)
        }
    }

    Column {
        Crossfade(targetState = model.type.value, modifier = Modifier.weight(1f)) {
            when (it) {
                JoinContent.Unknown -> Unit
                JoinContent.Auto -> JoinAsAuto(model)
                JoinContent.Guest -> JoinAsGuest(model)
                JoinContent.User -> JoinAsUser(model)
            }
        }
        JoinBottomBar()
    }
}

@Composable
private fun JoinBottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .background(color = Color.Black)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .requiredWidthIn(max = 320.dp),
        ) {
            val defaultModifier = Modifier
                .fillMaxHeight()
                .aspectRatio(ratio = 1f)

            SettingsIcon(modifier = defaultModifier)
            Spacer(modifier = defaultModifier)
            SpeakerPrivacyIcon(modifier = defaultModifier)
            MicrophonePrivacyIcon(modifier = defaultModifier)
            CameraPrivacyIcon(modifier = defaultModifier)
            Spacer(modifier = defaultModifier)
            CameraEffectIcon(modifier = defaultModifier)
        }
    }
}
