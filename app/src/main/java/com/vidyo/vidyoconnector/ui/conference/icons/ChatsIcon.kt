package com.vidyo.vidyoconnector.ui.conference.icons

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.Badge
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import dev.matrix.compose_routes.navigateToChatsScreen

@Composable
fun ChatsIcon(modifier: Modifier) {
    val navController = LocalNavController.current
    val chats = LocalConnectorManager.current.chats

    val unreadCounter = chats.unreadCounter.collectAsState().value
    val unreadCounterText = remember(unreadCounter) {
        unreadCounter.toUnreadCounterText()
    }

    Badge(text = unreadCounterText) {
        IconButton(
            onClick = { navController.navigateToChatsScreen() },
            modifier = modifier,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_chat),
                contentDescription = "chats",
            )
        }
    }
}

fun Int.toUnreadCounterText() = when (this) {
    0 -> ""
    in 1..9 -> toString()
    else -> "9+"
}
