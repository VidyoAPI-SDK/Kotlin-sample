package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ChatMessageBubbleType {
    Incoming,
    Outgoing,
    Special,
}

@Composable
fun ChatMessageBubble(text: String, type: ChatMessageBubbleType, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colors
    val color = when (type) {
        ChatMessageBubbleType.Incoming -> Color.LightGray
        ChatMessageBubbleType.Outgoing -> colors.primary
        ChatMessageBubbleType.Special -> Color.LightGray
    }
    Text(
        text = text,
        color = colors.onPrimary,
        modifier = modifier
            .background(color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
