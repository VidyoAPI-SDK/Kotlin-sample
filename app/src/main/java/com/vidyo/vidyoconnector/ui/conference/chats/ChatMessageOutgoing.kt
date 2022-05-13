package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.bl.connector.chats.ChatMessage

@Composable
fun ChatMessageOutgoing(
    message: ChatMessage.Outgoing,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        ChatMessageBubble(
            text = message.getDisplayText(),
            type = ChatMessageBubbleType.Outgoing,
        )
        ChatMessageTime(timestamp = message.realTimestamp)
    }
}
