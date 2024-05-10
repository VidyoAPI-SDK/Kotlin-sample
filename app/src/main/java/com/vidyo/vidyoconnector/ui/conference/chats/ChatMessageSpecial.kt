package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.vidyo.vidyoconnector.bl.connector.chats.ChatMessage
import java.text.SimpleDateFormat

@Composable
fun ChatMessageSpecial(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val time = remember {
        val locale = requireNotNull(ConfigurationCompat.getLocales(configuration)[0])
        val format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, locale)
        format.format(message.realTimestamp)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = time,
            color = Color.LightGray,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message.getDisplayText(),
            color = Color.LightGray,
        )
    }
}
