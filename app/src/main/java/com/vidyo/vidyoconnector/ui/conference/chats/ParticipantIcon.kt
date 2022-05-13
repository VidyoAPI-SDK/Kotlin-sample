package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.bl.connector.participants.Participant

@Composable
fun ParticipantIcon(
    participant: Participant,
    modifier: Modifier = Modifier,
    active: Boolean = false,
) {
    var boxModifier = modifier.background(Color.Black, RoundedCornerShape(percent = 50))
    if (active) {
        boxModifier = boxModifier.border(
            width = 3.dp,
            color = MaterialTheme.colors.primary,
            shape = RoundedCornerShape(percent = 50),
        )
    }

    Box(contentAlignment = Alignment.Center, modifier = boxModifier) {
        Text(
            text = participant.initials,
            fontSize = 18.sp,
            color = Color.White,
        )
    }
}
