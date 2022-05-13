package com.vidyo.vidyoconnector.ui.join

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.BuildConfig
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.ProtocolHandler
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceJoinInfo
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import com.vidyo.vidyoconnector.ui.utils.ProgressDialog
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcGreenButton
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcTextButton
import com.vidyo.vidyoconnector.ui.utils.styles.text_field.VcDefaultTextField
import dev.matrix.compose_routes.ComposableRoute
import dev.matrix.compose_routes.navigateToJoinAsUserScreen

private class JoinAsGuestStates(
    val portal: MutableState<String>,
    val name: MutableState<String>,
    val roomKey: MutableState<String>,
    val roomPin: MutableState<String>,
)

@Composable
@ComposableRoute
fun JoinAsGuestScreen() {
    val manager = LocalConnectorManager.current
    val states = JoinAsGuestStates(
        portal = rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_GUEST_PORTAL) },
        name = rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_GUEST_NAME) },
        roomKey = rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_GUEST_ROOM_KEY) },
        roomPin = rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_GUEST_ROOM_PIN) },
    )

    val conference = manager.conference.state.collectAsState()
    if (conference.value.isActive) {
        ProgressDialog()
    }

    LaunchedEffect("init") {
        val args = ProtocolHandler.consume()
        if (args == null || conference.value.isActive) {
            return@LaunchedEffect
        }

        states.portal.value = args.portal
        states.name.value = args.name
        states.roomKey.value = args.roomKey
        states.roomPin.value = args.roomPin

        manager.media.localCamera.requestMutedState(args.muteCamera)
        manager.media.localSpeaker.requestMutedState(args.muteSpeaker)
        manager.media.localMicrophone.requestMutedState(args.muteMicrophone)

        if (args.autoJoin) {
            performJoin(manager.conference, states)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val modifier = Modifier
            .fillMaxWidth()
            .weight(weight = 1f)

        val orientation = LocalConfiguration.current.orientation
        when (orientation == Configuration.ORIENTATION_PORTRAIT) {
            true -> Portrait(states = states, modifier = modifier)
            else -> Landscape(states = states, modifier = modifier)
        }

        JoinBottomBar()
    }
}

@Composable
private fun Portrait(modifier: Modifier = Modifier, states: JoinAsGuestStates) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .requiredWidthIn(max = 320.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_vidyo_platform_logo),
            contentDescription = "logo",
            modifier = Modifier
                .height(120.dp)
                .padding(vertical = 16.dp),
        )

        FormFields(states = states)

        Spacer(modifier = Modifier.height(16.dp))
        Buttons(states = states)

        Spacer(modifier = Modifier.height(2.dp))
        SelfView()
    }
}

@Composable
private fun Landscape(modifier: Modifier = Modifier, states: JoinAsGuestStates) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp),
    ) {
        FormFields(
            states = states,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
        ) {
            SelfView(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))
            Buttons(states = states)
        }
    }
}

@Composable
private fun FormFields(modifier: Modifier = Modifier, states: JoinAsGuestStates) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        val defaultModifier = Modifier.fillMaxWidth()

        Text(
            text = stringResource(R.string.join_welcome_text),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.joinAsGuest_portal)) },
            singleLine = true,
            value = states.portal.value,
            onValueChange = { states.portal.value = it },
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.joinAsGuest_name)) },
            singleLine = true,
            value = states.name.value,
            onValueChange = { states.name.value = it },
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = defaultModifier) {
            VcDefaultTextField(
                label = { Text(text = stringResource(R.string.joinAsGuest_roomKey)) },
                singleLine = true,
                value = states.roomKey.value,
                onValueChange = { states.roomKey.value = it },
                modifier = Modifier.weight(weight = 1f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            VcDefaultTextField(
                label = { Text(text = stringResource(R.string.joinAsGuest_roomPin)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                value = states.roomPin.value,
                onValueChange = { states.roomPin.value = it },
                modifier = Modifier.weight(weight = 1f),
            )
        }
    }
}

@Composable
private fun SelfView(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        LocalConnectorManager.current.PreviewView(
            modifier = Modifier
                .requiredHeightIn(min = 120.dp)
                .aspectRatio(16f / 9f, matchHeightConstraintsFirst = true),
        )
    }
}

@Composable
private fun Buttons(modifier: Modifier = Modifier, states: JoinAsGuestStates) {
    val navController = LocalNavController.current
    val conference = LocalConnectorManager.current.conference
    val conferenceState = conference.state.collectAsState()

    Column(modifier = modifier) {
        val defaultModifier = Modifier
            .fillMaxWidth()
            .height(50.dp)

        VcGreenButton(
            onClick = { performJoin(conference, states) },
            modifier = defaultModifier,
            enabled = !conferenceState.value.isActive,
        ) {
            Text(text = stringResource(R.string.joinAsGuest_join))
        }

        VcTextButton(
            onClick = {
                navController.navigateToJoinAsUserScreen(
                    portal = states.portal.value,
                    username = states.name.value,
                    roomKey = states.roomKey.value,
                    roomPin = states.roomPin.value,
                )
            },
            modifier = defaultModifier,
        ) {
            Text(text = stringResource(R.string.joinAsGuest_joinAsUser))
        }
    }
}

private fun performJoin(manager: ConferenceManager, states: JoinAsGuestStates) {
    val info = ConferenceJoinInfo.AsGuest(
        portal = states.portal.value,
        name = states.name.value,
        roomKey = states.roomKey.value,
        roomPin = states.roomPin.value,
    )
    manager.join(info)
}
