package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceJoinInfo
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun AutoJoinOverlayIcon(visible: MutableState<Boolean>, modifier: Modifier) {
    val conference = LocalConnectorManager.current.conference.conference.collectAsState().value
    if (conference.joinInfo !is ConferenceJoinInfo.AsAuto) {
        return
    }

    IconButton(
        onClick = { visible.value = true },
        modifier = modifier,
    ) {
        Image(
            painter = rememberVectorPainter(Icons.Default.Share),
            contentDescription = "show auto join overlay",
            colorFilter = ColorFilter.tint(color = Color.White),
        )
    }
}
