package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.chats.Chat
import com.vidyo.vidyoconnector.ui.utils.Badge
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.LocalNavController
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import com.vidyo.vidyoconnector.ui.utils.styles.text_field.VcDefaultTextField
import com.vidyo.vidyoconnector.ui.conference.icons.toUnreadCounterText
import dev.matrix.compose_routes.ComposableRoute
import dev.matrix.compose_routes.navigateToChatScreen

@Composable
@ComposableRoute
fun ChatsScreen() {
    Scaffold(
        topBar = { AppBar() },
        backgroundColor = Color(0xff353535),
        contentColor = Color.White,
    ) {
        val filter = rememberSaveable { mutableStateOf("") }
        val chats = LocalConnectorManager.current.chats.all.collectAsState().value

        Column(modifier = Modifier.fillMaxSize()) {
            VcDefaultTextField(
                value = filter.value,
                onValueChange = { filter.value = it },
                placeholder = { Text(text = stringResource(R.string.chat_participantNameHint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val values = chats.filter {
                    it.participant == null || it.participant.name.contains(filter.value)
                }
                items(values.size, key = { values[it].id }) {
                    ChatItem(
                        chat = values[it],
//                    modifier = Modifier.animateItemPlacement(), TODO
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.chat_header)) },
        navigationIcon = { NavBackIcon() },
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
    )
}

@Composable
private fun ChatItem(modifier: Modifier = Modifier, chat: Chat) {
    val navController = LocalNavController.current
    val active = chat.active.collectAsState()
    val lastMessage = chat.messages.collectAsState().value.lastOrNull()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { navController.navigateToChatScreen(chat.id) }
            .padding(horizontal = 22.dp, vertical = 8.dp),
    ) {
        ChatItemIcon(chat = chat)

        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.participant?.name ?: stringResource(R.string.chat_headerGroup),
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            val message = when {
                !active.value -> stringResource(R.string.chat_participantLeft)
                lastMessage != null -> lastMessage.getDisplayText()
                else -> null
            }

            if (message != null) {
                Text(
                    text = message,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun ChatItemIcon(chat: Chat, modifier: Modifier = Modifier) {
    val unreadCounter = chat.unreadCounter.collectAsState().value
    val unreadCounterText = remember(unreadCounter) {
        unreadCounter.toUnreadCounterText()
    }

    Badge(text = unreadCounterText, modifier = modifier.size(42.dp)) {
        if (chat.participant != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black, shape = RoundedCornerShape(percent = 50))
            ) {
                Text(text = chat.participant.initials, fontSize = 18.sp)
            }
        } else {
            Image(
                painter = painterResource(R.drawable.ic_chat),
                contentDescription = "group chat",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
