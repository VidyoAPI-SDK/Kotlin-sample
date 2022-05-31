package com.vidyo.vidyoconnector.ui.conference

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.base.Camera
import com.vidyo.vidyoconnector.ui.conference.ptz.CameraControlsPanel
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.conference.icons.*
import dev.matrix.compose_routes.ComposableRoute

private val TOOLBAR_ROW_HEIGHT = 50.dp

private val toolbarInAnimation = fadeIn() + expandIn(initialSize = {
    IntSize(it.width, 0)
})

private val toolbarOutAnimation = fadeOut() + shrinkOut(targetSize = {
    IntSize(it.width, 0)
})

@Composable
@ComposableRoute
@Preview
fun ConferenceScreen() {
    val manager = LocalConnectorManager.current
    val moreOptions = remember { mutableStateOf(false) }
    val cameraControlsActive = remember { mutableStateOf<Camera?>(null) }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (jumpBar, jumpBarPopup, video, message, ptz) = createRefs()

        manager.ConferenceView(modifier = Modifier.constrainAs(video) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(parent.top)
            bottom.linkTo(jumpBar.top)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        })

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .constrainAs(message) {
                    top.linkTo(parent.top, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
                .padding(horizontal = 16.dp),
        ) {
            val screenShareActive = manager.media.localScreenShare.active.collectAsState()
            if (screenShareActive.value) {
                TooltipMessage(message = stringResource(R.string.conference_shareActive))
            }

            val audioOnly = manager.media.audioOnly.collectAsState()
            if (audioOnly.value) {
                TooltipMessage(message = stringResource(R.string.conference_audioOnly))
            }
        }

        JumpBar(
            moreOptions = moreOptions,
            modifier = Modifier.constrainAs(jumpBar) {
                width = Dimension.fillToConstraints
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
        )

        Box(modifier = Modifier.constrainAs(jumpBarPopup) {
            width = Dimension.fillToConstraints
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(jumpBar.top)
        }) {
            AnimatedVisibility(
                visible = moreOptions.value,
                enter = toolbarInAnimation,
                exit = toolbarOutAnimation,
            ) {
                JumpBarPopup(cameraControlsActive = cameraControlsActive)
            }
        }

        val camera = cameraControlsActive.value
        if (camera != null) {
            CameraControlsPanel(
                camera = camera,
                onClose = { cameraControlsActive.value = null },
                modifier = Modifier.constrainAs(ptz) {
                    end.linkTo(parent.end)
                    bottom.linkTo(jumpBarPopup.top)
                }
            )
        }
    }
}

@Composable
private fun JumpBar(modifier: Modifier = Modifier, moreOptions: MutableState<Boolean>) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .height(TOOLBAR_ROW_HEIGHT)
            .background(Color.Black),
    ) {
        val defaultModifier = Modifier
            .fillMaxHeight()
            .aspectRatio(ratio = 1f)

        val connector = LocalConnectorManager.current
        JumpBarIcon(
            modifier = defaultModifier,
            icon = R.drawable.ic_end_call,
            contentDescription = "end call",
            onClick = { connector.conference.disconnect() },
        )

        SettingsIcon(modifier = defaultModifier)
        SpeakerPrivacyIcon(modifier = defaultModifier)
        MicrophonePrivacyIcon(modifier = defaultModifier)
        CameraPrivacyIcon(modifier = defaultModifier)

        JumpBarIcon(
            modifier = defaultModifier,
            icon = R.drawable.ic_more_options,
            contentDescription = "more options",
            onClick = { moreOptions.value = !moreOptions.value },
        )
    }
}

@Composable
private fun JumpBarPopup(
    modifier: Modifier = Modifier,
    cameraControlsActive: MutableState<Camera?>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .height(TOOLBAR_ROW_HEIGHT)
            .background(Color.Black),
    ) {
        val defaultModifier = Modifier
            .fillMaxHeight()
            .aspectRatio(ratio = 1f)

        JumpBarIcon(
            modifier = defaultModifier,
            icon = R.drawable.ic_moderator_controls,
            contentDescription = "moderator controls",
            onClick = { /*TODO*/ },
        )

        ScreenShareIcon(modifier = defaultModifier)
        ParticipantsIcon(modifier = defaultModifier)
        ChatsIcon(modifier = defaultModifier)

        CameraEffectIcon(modifier = defaultModifier)
        CameraControlsIcon(modifier = defaultModifier, state = cameraControlsActive)
    }
}

@Composable
private fun JumpBarIcon(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Image(painter = painterResource(icon), contentDescription = contentDescription)
    }
}

@Composable
private fun TooltipMessage(modifier: Modifier = Modifier, message: String) {
    Text(
        text = message,
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
