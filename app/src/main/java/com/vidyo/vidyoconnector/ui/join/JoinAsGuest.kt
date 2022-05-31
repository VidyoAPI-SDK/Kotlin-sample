package com.vidyo.vidyoconnector.ui.join

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcGreenButton
import com.vidyo.vidyoconnector.ui.utils.styles.buttons.VcTextButton
import com.vidyo.vidyoconnector.ui.utils.styles.text_field.VcDefaultTextField

@Composable
fun JoinAsGuest(model: JoinViewModel) {
    val modifier = Modifier.fillMaxSize()
    val orientation = LocalConfiguration.current.orientation

    when (orientation == Configuration.ORIENTATION_PORTRAIT) {
        true -> Portrait(modifier = modifier, model = model)
        else -> Landscape(modifier = modifier, model = model)
    }
}

@Composable
private fun Portrait(modifier: Modifier = Modifier, model: JoinViewModel) {
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

        FormFields(model = model)

        Spacer(modifier = Modifier.height(16.dp))
        Buttons(model = model)

        Spacer(modifier = Modifier.height(16.dp))
        SelfView()
    }
}

@Composable
private fun Landscape(modifier: Modifier = Modifier, model: JoinViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp),
    ) {
        FormFields(
            model = model,
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
            Buttons(model = model)
        }
    }
}

@Composable
private fun FormFields(modifier: Modifier = Modifier, model: JoinViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        val defaultModifier = Modifier.fillMaxWidth()

        Text(
            text = stringResource(R.string.join_welcome),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.join_portal)) },
            singleLine = true,
            value = model.portal.value,
            onValueChange = { model.portal.value = it },
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        VcDefaultTextField(
            label = { Text(text = stringResource(R.string.join_displayName)) },
            singleLine = true,
            value = model.username.value,
            onValueChange = { model.username.value = it },
            modifier = defaultModifier,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = defaultModifier) {
            VcDefaultTextField(
                label = { Text(text = stringResource(R.string.join_roomKey)) },
                singleLine = true,
                value = model.roomKey.value,
                onValueChange = { model.roomKey.value = it },
                modifier = Modifier.weight(weight = 1f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            VcDefaultTextField(
                label = { Text(text = stringResource(R.string.join_roomPin)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                value = model.roomPin.value,
                onValueChange = { model.roomPin.value = it },
                modifier = Modifier.weight(weight = 1f),
            )
        }
    }
}

@Composable
private fun Buttons(modifier: Modifier = Modifier, model: JoinViewModel) {
    val conference = LocalConnectorManager.current.conference
    val conferenceState = conference.conference.collectAsState()
    val valid = model.portal.value.isNotEmpty()
            && model.username.value.isNotEmpty()
            && model.roomKey.value.isNotEmpty()
            && model.roomPin.value.isNotEmpty()

    Column(modifier = modifier) {
        val defaultModifier = Modifier.height(50.dp)

        VcGreenButton(
            onClick = { model.join(conference) },
            modifier = defaultModifier.fillMaxWidth(),
            enabled = valid && !conferenceState.value.state.isActive,
        ) {
            Text(text = stringResource(R.string.join_joinAsGuest).replace("\n", " "))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            VcTextButton(
                onClick = { model.type.value = JoinContent.Auto },
                modifier = defaultModifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.join_joinAsAuto), textAlign = TextAlign.Center)
            }

            VcTextButton(
                onClick = { model.type.value = JoinContent.User },
                modifier = defaultModifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.join_joinAsUser), textAlign = TextAlign.Center)
            }
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
