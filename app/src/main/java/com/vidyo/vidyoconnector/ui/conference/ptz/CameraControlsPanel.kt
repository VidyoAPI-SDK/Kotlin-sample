package com.vidyo.vidyoconnector.ui.conference.ptz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.base.Camera
import com.vidyo.vidyoconnector.bl.connector.media.base.CameraControlAction
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun CameraControlsPanel(camera: Camera, onClose: () -> Unit, modifier: Modifier = Modifier) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White,
        LocalRippleTheme provides CustomRippleTheme,
    ) {
        Column(modifier = modifier.width(182.dp)) {
            Title(camera, onClose, modifier = Modifier.fillMaxWidth())
            Controls(camera, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun Title(camera: Camera, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val participants = LocalConnectorManager.current.participants
    val cameraName = remember { mutableStateOf("") }

    if (camera.participantId.isNotEmpty()) {
        LaunchedEffect(camera.participantId) {
            val participant = participants.findParticipant(camera.participantId)
            if (participant != null) {
                cameraName.value = participant.name
            }
        }
    } else {
        cameraName.value = stringResource(R.string.cameraControlsIcon_localCamera, camera.name)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(44.dp)
            .background(Color.Black, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_grabbty_texture),
            contentDescription = "grab",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 6.dp, vertical = 6.dp),
        )
        Text(
            text = cameraName.value,
            color = Color.White,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )
        IconButton(
            onClick = { onClose() },
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f, matchHeightConstraintsFirst = true),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "close",
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun Controls(camera: Camera, modifier: Modifier = Modifier) {
    val controls = remember(camera) { CameraControlsLogic(camera) }

    LaunchedEffect(controls) {
        controls.startWorker()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color(0xB0353535), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        PanTilt(controls)
        Zoom(controls)
    }
}

@Composable
private fun PanTilt(controls: CameraControlsLogic, modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black, CircleShape)
            .border(3.dp, Color.White, CircleShape)
    ) {
        val size = Dimension.percent(.33f)
        val (up, down, left, right) = createRefs()
        val capabilities = controls.camera.controlCapabilities.collectAsState().value

        IconButton(
            onClick = { },
            enabled = capabilities.hasPanTilt,
            modifier = Modifier
                .constrainAs(up) {
                    width = size
                    height = size
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .pointerInput(controls) {
                    controls.handleEvent(this, CameraControlAction.TiltUp)
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_up),
                contentDescription = "tilt up",
            )
        }

        IconButton(
            onClick = { },
            enabled = capabilities.hasPanTilt,
            modifier = Modifier
                .constrainAs(down) {
                    width = size
                    height = size
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .pointerInput(controls) {
                    controls.handleEvent(this, CameraControlAction.TiltDown)
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = "tilt down",
            )
        }

        IconButton(
            onClick = { },
            enabled = capabilities.hasPanTilt,
            modifier = Modifier
                .constrainAs(left) {
                    width = size
                    height = size
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .pointerInput(controls) {
                    controls.handleEvent(this, CameraControlAction.PanLeft)
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "pan left",
            )
        }

        IconButton(
            onClick = { },
            enabled = capabilities.hasPanTilt,
            modifier = Modifier
                .constrainAs(right) {
                    width = size
                    height = size
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .pointerInput(controls) {
                    controls.handleEvent(this, CameraControlAction.PanRight)
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "pan right",
            )
        }
    }
}

@Composable
private fun Zoom(controls: CameraControlsLogic, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(Color.Black, CircleShape)
            .border(1.dp, Color.White, CircleShape)
    ) {
        val capabilities = controls.camera.controlCapabilities.collectAsState().value

        IconButton(
            onClick = { },
            enabled = capabilities.hasZoom,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .pointerInput(controls) {
                    controls.handleEvent(this, CameraControlAction.ZoomOut)
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_zoom_out),
                contentDescription = "zoom out",
            )
        }

        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(color = Color.White),
        )

        IconButton(
            onClick = { },
            enabled = capabilities.hasZoom,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .pointerInput(controls) {
                    controls.handleEvent(this, CameraControlAction.ZoomIn)
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_zoom_in),
                contentDescription = "zoom in",
            )
        }
    }
}

private object CustomRippleTheme : RippleTheme {
    val color = RippleTheme.defaultRippleColor(Color.White, false)
    val alpha = RippleTheme.defaultRippleAlpha(Color.White, false)

    @Composable
    override fun defaultColor(): Color {
        return color
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return alpha
    }
}
