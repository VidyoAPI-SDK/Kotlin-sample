package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.bl.connector.chats.ChatMessage

@Composable
fun ChatMessageIncoming(
    message: ChatMessage.Incoming,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        ParticipantIcon(
            participant = message.sender,
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = message.sender.name,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            ChatMessageBubble(
                text = message.getDisplayText(),
                type = ChatMessageBubbleType.Incoming,
            )

            ChatMessageTime(timestamp = message.realTimestamp)
        }
    }
}
