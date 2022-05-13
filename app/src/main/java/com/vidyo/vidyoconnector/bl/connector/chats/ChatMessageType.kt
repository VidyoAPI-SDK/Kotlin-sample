package com.vidyo.vidyoconnector.bl.connector.chats

import com.vidyo.VidyoClient.Endpoint.ChatMessage.ChatMessageType as VcChatMessageType

enum class ChatMessageType(
    val isMessage: Boolean,
    val jniValue: VcChatMessageType,
) {
    Chat(
        isMessage = true,
        jniValue = VcChatMessageType.VIDYO_CHATMESSAGETYPE_Chat
    ),
    MediaStart(
        isMessage = true,
        jniValue = VcChatMessageType.VIDYO_CHATMESSAGETYPE_MediaStart
    ),
    MediaStop(
        isMessage = true,
        jniValue = VcChatMessageType.VIDYO_CHATMESSAGETYPE_MediaStop
    ),
    PrivateChat(
        isMessage = true,
        jniValue = VcChatMessageType.VIDYO_CHATMESSAGETYPE_PrivateChat
    );

    companion object {
        fun from(type: VcChatMessageType) = values().find { it.jniValue == type } ?: Chat
    }
}
