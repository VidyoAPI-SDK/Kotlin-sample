package com.vidyo.vidyoconnector.bl.connector.chats

import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.media.MediaManager
import com.vidyo.vidyoconnector.bl.connector.participants.Participant
import com.vidyo.vidyoconnector.bl.connector.participants.ParticipantsManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.collectInScopeLatest
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import kotlinx.coroutines.flow.*

class Chat(
    val participant: Participant?,
    private val scope: ConnectorScope,
    events: Flow<ChatMessage>,
    mediaManager: MediaManager,
    participants: ParticipantsManager,
) {
    private val messagesList = ArrayList<ChatMessage>()
    private val messagesTrigger = MutableStateFlow(0L)
    private val messagesState = MutableStateFlow(emptyList<ChatMessage>())
    private val unreadCounterState = MutableStateFlow(0)

    private var lastReadTimestamp = 0L

    val id = participant?.id.orEmpty()
    val active: StateFlow<Boolean>
    val messages = messagesState.asStateFlow()
    val unreadCounter = unreadCounterState.asStateFlow()

    init {
        events.collectInScope(scope) {
            insertMessage(it)
        }

        active = when (participant == null) {
            true -> MutableStateFlow(true)
            else -> participants.trackParticipantAvailable(participant.id)
                .stateIn(scope, SharingStarted.Lazily, true)
        }

        messagesTrigger.collectInScopeLatest(scope) {
            messagesState.value = ArrayList(messagesList)
        }

        participants.onJoined
            .filter { !it.isLocal }
            .filter { participant == null || participant.id == it.id }
            .collectInScope(scope) {
                insertMessage(ChatMessage.ParticipantJoined(it))
            }

        participants.onLeft
            .filter { !it.isLocal }
            .filter { participant == null || participant.id == it.id }
            .collectInScope(scope) {
                insertMessage(ChatMessage.ParticipantLeft(it))
            }

        mediaManager.remoteScreenShare.onStarted
            .filter { participant == null || participant.id == it.participant.id }
            .collectInScope(scope) {
                insertMessage(ChatMessage.ScreenShareStarted(it))
            }

        mediaManager.remoteScreenShare.onStopped
            .filter { participant == null || participant.id == it.participant.id }
            .collectInScope(scope) {
                insertMessage(ChatMessage.ScreenShareStopped(it))
            }
    }

    fun sendMessage(message: String) = scope.run {
        when (participant == null) {
            true -> scope.connector.sendChatMessage(message)
            else -> scope.connector.sendPrivateChatMessage(participant.handle, message)
        }

        val lastMessage = messagesList.lastOrNull()
        val realTimestamp = System.currentTimeMillis()
        val orderTimestamp = when (lastMessage != null) {
            true -> lastMessage.orderTimestamp + 1
            else -> realTimestamp
        }

        val chatMessage = ChatMessage.Outgoing(
            text = message,
            realTimestamp = realTimestamp,
            orderTimestamp = orderTimestamp,
        )
        insertMessage(chatMessage)
    }

    fun markMessageAsRead(message: ChatMessage) = scope.run {
        if (message.orderTimestamp < lastReadTimestamp) {
            return@run
        }
        lastReadTimestamp = message.orderTimestamp
        updateUnreadCounter()
    }

    private fun updateUnreadCounter() {
        val lastIndex = messagesList.indexOfFirst { lastReadTimestamp < it.orderTimestamp }
        if (lastIndex < 0) {
            unreadCounterState.value = 0
            return
        }

        var counter = 0
        for (index in lastIndex until messagesList.size) {
            if (messagesList[index].affectsUnreadCounter) ++counter
        }
        unreadCounterState.value = counter
    }

    private fun insertMessage(message: ChatMessage) {
        var index = messagesList.binarySearch {
            it.orderTimestamp.compareTo(message.orderTimestamp)
        }
        if (index < 0) {
            index = -index - 1
        }
        messagesList.add(index, message)
        messagesTrigger.trigger()

        if (message.affectsUnreadCounter) {
            updateUnreadCounter()
        }
    }
}
