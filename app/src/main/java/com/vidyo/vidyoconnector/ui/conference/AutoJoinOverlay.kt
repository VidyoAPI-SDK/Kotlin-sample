package com.vidyo.vidyoconnector.ui.conference

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceJoinInfo
import com.vidyo.vidyoconnector.ui.utils.LocalActivity
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutoJoinOverlay(visible: MutableState<Boolean>) {
    if (!visible.value) {
        return
    }

    val joinInfo = LocalConnectorManager.current.conference.conference.collectAsState().value.joinInfo
    if (joinInfo !is ConferenceJoinInfo.AsAuto) {
        return
    }

    val activity = LocalActivity.current
    fun buildShareIntent() = Intent()
        .setType("text/plain")
        .setAction(Intent.ACTION_SEND)
        .putExtra(Intent.EXTRA_TEXT, joinInfo.roomInfo.roomUrl)

    Dialog(
        onDismissRequest = { visible.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.conference_welcome),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = joinInfo.roomInfo.inviteContent,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .background(Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = joinInfo.roomInfo.roomUrl,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.conference_roomPin, joinInfo.roomInfo.pin),
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { activity.startActivity(Intent.createChooser(buildShareIntent(), null)) }) {
                    Icon(Icons.Default.Share, contentDescription = "share conference link", tint = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = { visible.value = false }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        }
    }
}
