package com.vidyo.vidyoconnector.ui.conference.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.chats.Chat
import com.vidyo.vidyoconnector.bl.connector.chats.ChatMessage
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import com.vidyo.vidyoconnector.ui.utils.styles.text_field.VcDefaultTextField
import dev.matrix.compose_routes.ComposableRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
@ComposableRoute
fun ChatScreen(id: String?) {
    val manager = LocalConnectorManager.current.chats
    val chat = remember { mutableStateOf<Chat?>(null) }

    LaunchedEffect(id) {
        chat.value = manager.findChat(id.orEmpty())
    }

    val chatValue = chat.value
    if (chatValue != null) {
        Scaffold(
            topBar = { AppBar(chatValue) },
            backgroundColor = Color(0xff353535),
            contentColor = Color.White,
        ) {
            Content(chat = chatValue, modifier = Modifier.background(Color(0xff353535)))
        }
    }
}

@Composable
private fun Content(chat: Chat, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Messages(
            chat = chat,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )

        BottomPanel(chat = chat)
    }
}

@Composable
private fun AppBar(chat: Chat, modifier: Modifier = Modifier) {
    val manager = LocalConnectorManager.current
    val count = manager.participants.all.collectAsState().value.size

    var title = stringResource(R.string.chat_headerGroup)
    if (chat.participant != null) {
        title = chat.participant.name
    } else if (count > 0) {
        title = "$title ($count)"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        if (chat.participant != null) {
            ParticipantIcon(
                participant = chat.participant,
                modifier = Modifier.size(50.dp),
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_chat),
                contentDescription = "group chat",
                modifier = Modifier.size(50.dp),
            )
        }
        TopAppBar(
            title = { Text(text = title, color = Color.White) },
            navigationIcon = { NavBackIcon() },
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
        )
    }
}

@Composable
private fun Messages(chat: Chat, modifier: Modifier = Modifier) {
    class AutoScrollInfo {
        var lastItemsCount = 0
        val channel = Channel<Unit>(Channel.CONFLATED)
    }

    val messages = chat.messages.collectAsState()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (messages.value.size - 1).coerceAtLeast(0)
    )

    val info = remember { AutoScrollInfo() }
    LaunchedEffect(chat) {
        info.channel.consumeAsFlow().collectLatest {
            listState.animateScrollToItem(messages.value.size - 1)
        }
    }

    if (info.lastItemsCount != messages.value.size) {
        val visible = listState.layoutInfo.visibleItemsInfo.any { item ->
            item.index == info.lastItemsCount - 1
        }
        if (visible) {
            info.channel.trySend(Unit)
        }
        info.lastItemsCount = messages.value.size
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.background(Color.White),
    ) {
        val itemModifier = Modifier.fillMaxWidth()
        items(messages.value.size) {
            val message = messages.value[it]
            when (message) {
                is ChatMessage.Incoming -> {
                    ChatMessageIncoming(modifier = itemModifier, message = message)
                }
                is ChatMessage.Outgoing -> {
                    ChatMessageOutgoing(modifier = itemModifier, message = message)
                }
                else -> {
                    ChatMessageSpecial(modifier = itemModifier, message = message)
                }
            }
            chat.markMessageAsRead(message)
        }
    }
}

@Composable
private fun BottomPanel(chat: Chat, modifier: Modifier = Modifier) {
    val active = chat.active.collectAsState()

    val message = rememberSaveable { mutableStateOf("") }
    val messageTooShort = message.value.isBlank()
    val messageTooLong = !messageTooShort && message.value.length > 1024
    val messageIsValid = !messageTooShort && !messageTooLong

    if (active.value) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(
                start = 24.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 20.dp
            ),
        ) {
            VcDefaultTextField(
                value = message.value,
                onValueChange = { message.value = it },
                placeholder = { Text(stringResource(R.string.chat_messageHint)) },
                label = when (messageTooLong) {
                    true -> {
                        { Text(stringResource(R.string.chat_messageError)) }
                    }
                    else -> null
                },
                isError = messageTooLong,
                enabled = active.value,
                colors = VcDefaultTextField.defaultWhiteColors(),
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    chat.sendMessage(message.value)
                    message.value = ""
                },
                enabled = active.value && messageIsValid,
                modifier = Modifier.size(42.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_message_send),
                    contentDescription = "send message",
                    alpha = if (messageIsValid) 1f else .5f,
                )
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .height(68.dp),
        ) {
            Text(
                text = stringResource(R.string.chat_participantLeftHint),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.White,
            )
        }
    }
}
