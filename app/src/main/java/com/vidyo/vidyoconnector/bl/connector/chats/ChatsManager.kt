package com.vidyo.vidyoconnector.bl.connector.chats

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import com.vidyo.vidyoconnector.bl.connector.media.MediaManager
import com.vidyo.vidyoconnector.bl.connector.participants.Participant
import com.vidyo.vidyoconnector.bl.connector.participants.ParticipantsManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.collectInScopeLatest
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.vidyo.VidyoClient.Endpoint.ChatMessage as VcChatMessage
import com.vidyo.VidyoClient.Endpoint.Participant as VcParticipant

class ChatsManager(
    private val scope: ConnectorScope,
    private val mediaManager: MediaManager,
    private val participants: ParticipantsManager,
    conferenceManager: ConferenceManager,
) {
    private val map = HashMap<String, ChatState>()
    private val mapTrigger = MutableStateFlow(0L)
    private val listState = MutableStateFlow(emptyList<Chat>())
    private val unreadCounterState = MutableStateFlow(0)

    val all = listState.asStateFlow()
    val unreadCounter = unreadCounterState.asStateFlow()

    init {
        scope.connector.registerMessageEventListener(MessageEventListener())

        mapTrigger.debounce(500).collectInScopeLatest(scope) {
            listState.value = map.values.map { it.chat }

            combine(map.values.map { it.chat.unreadCounter }) { it.sum() }
                .collect {
                    unreadCounterState.value = it
                }
        }

        participants.onJoined.collectInScope(scope) {
            if (!it.isLocal) ensureChat(it)
        }

        conferenceManager.conference
            .map { it.state.isActive }
            .distinctUntilChanged()
            .filter { !it }
            .collectInScopeLatest(scope) {
                map.values.forEach { it.scope.cancel() }
                map.clear()
                mapTrigger.trigger()
                ensureChat(null)
            }
    }

    suspend fun findChat(id: String): Chat {
        return withContext(scope.dispatcher) {
            val chat = map[id]
            if (chat != null) {
                return@withContext chat.chat
            }

            val participant = participants.findParticipant(id)
            if (participant != null) {
                return@withContext ensureChat(participant).chat
            }

            ensureChat(null).chat
        }
    }

    private fun ensureChat(participant: Participant?): ChatState {
        val chatId = participant?.id.orEmpty()
        var chat = map[chatId]
        if (chat == null) {
            chat = ChatState(participant)
            map[chatId] = chat
            mapTrigger.trigger()
        }
        return chat
    }

    private inner class ChatState(participant: Participant?) {
        val scope = this@ChatsManager.scope.newChildScope()
        val events = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
        val chat = Chat(participant, scope, events, mediaManager, participants)
    }

    private inner class MessageEventListener : Connector.IRegisterMessageEventListener {
        override fun onChatMessageReceived(participant: VcParticipant?, chatMessage: VcChatMessage) {
            participant ?: return

            val type = ChatMessageType.from(chatMessage.type)
            if (!type.isMessage) {
                return
            }
            val private = type == ChatMessageType.PrivateChat
            val message = ChatMessage.Incoming(
                sender = Participant.from(participant),
                senderName = chatMessage.userName,
                text = chatMessage.body,
                realTimestamp = TimeUnit.NANOSECONDS.toMillis(chatMessage.timestamp),
            )
            scope.run {
                ensureChat(message.sender.takeIf { private }).events.tryEmit(message)
            }
        }
    }
}
