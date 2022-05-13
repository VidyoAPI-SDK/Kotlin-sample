package com.vidyo.vidyoconnector.ui.join

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceJoinInfo
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import com.vidyo.vidyoconnector.ui.utils.ProgressDialog
import com.vidyo.vidyoconnector.ui.utils.clearBackStack
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcGreenButton
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcTextButton
import com.vidyo.vidyoconnector.ui.utils.styles.text_field.VcDefaultTextField
import dev.matrix.compose_routes.ComposableRoute
import dev.matrix.compose_routes.NavRoutes

private class JoinAsUserStates(
    val portal: MutableState<String>,
    val username: MutableState<String>,
    val password: MutableState<String>,
    val roomKey: MutableState<String>,
    val roomPin: MutableState<String>,
)

@Composable
@ComposableRoute
fun JoinAsUserScreen(portal: String?, username: String?, roomKey: String?, roomPin: String?) {
    val conference = LocalConnectorManager.current.conference.state.collectAsState()
    if (conference.value.isActive) {
        ProgressDialog()
    }

    val states = JoinAsUserStates(
        portal = rememberSaveable { mutableStateOf(portal.orEmpty()) },
        username = rememberSaveable { mutableStateOf(username.orEmpty()) },
        password = rememberSaveable { mutableStateOf("") },
        roomKey = rememberSaveable { mutableStateOf(roomKey.orEmpty()) },
        roomPin = rememberSaveable { mutableStateOf(roomPin.orEmpty()) },
    )

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
private fun Portrait(modifier: Modifier = Modifier, states: JoinAsUserStates) {
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
            modifier = Modifier.height(120.dp).padding(vertical = 16.dp),
        )

        FormFields(states = states)

        Spacer(modifier = Modifier.height(16.dp))
        Buttons(states = states)

        Spacer(modifier = Modifier.height(16.dp))
        SelfView()
    }
}

@Composable
private fun Landscape(modifier: Modifier = Modifier, states: JoinAsUserStates) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp),
    ) {
        FormFields(
            states = states,
            modifier = Modifier
                .weight(weight = 1f)
                .verticalScroll(rememberScrollState()),
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
private fun FormFields(modifier: Modifier = Modifier, states: JoinAsUserStates) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        val defaultModifier = Modifier.fillMaxWidth()

        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.joinAsGuest_portal)) },
            singleLine = true,
            value = states.portal.value,
            onValueChange = { states.portal.value = it },
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.joinAsUser_username)) },
            singleLine = true,
            value = states.username.value,
            onValueChange = { states.username.value = it },
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.joinAsUser_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            value = states.password.value,
            onValueChange = { states.password.value = it },
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
private fun Buttons(modifier: Modifier = Modifier, states: JoinAsUserStates) {
    val navController = LocalNavController.current
    val conference = LocalConnectorManager.current.conference
    val state = conference.state.collectAsState()

    Column(modifier = modifier) {
        val defaultModifier = Modifier
            .fillMaxWidth()
            .height(50.dp)

        VcGreenButton(
            onClick = {
                val info = ConferenceJoinInfo.AsUser(
                    portal = states.portal.value,
                    username = states.username.value,
                    password = states.password.value,
                    roomKey = states.roomKey.value,
                    roomPin = states.roomPin.value,
                )
                conference.join(info)
            },
            enabled = !state.value.isActive,
            modifier = defaultModifier,
        ) {
            Text(text = stringResource(R.string.joinAsGuest_joinAsUser))
        }

        VcTextButton(
            onClick = {
                navController.navigate(NavRoutes.JoinAsGuestScreen()) { clearBackStack(navController) }
            },
            modifier = defaultModifier,
        ) {
            Text(text = stringResource(R.string.joinAsGuest_join))
        }
    }
}
