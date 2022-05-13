package com.vidyo.vidyoconnector.bl.connector.chats

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.media.remote.share.RemoteScreenShare
import com.vidyo.vidyoconnector.bl.connector.participants.Participant

sealed class ChatMessage(val affectsUnreadCounter: Boolean = false) {
    abstract val realTimestamp: Long
    abstract val orderTimestamp: Long

    @Composable
    abstract fun getDisplayText(): String

    data class Incoming(
        val sender: Participant,
        val senderName: String,
        val text: String,
        override val realTimestamp: Long,
    ) : ChatMessage(affectsUnreadCounter = true) {
        override val orderTimestamp = realTimestamp

        @Composable
        override fun getDisplayText() = text
    }

    data class Outgoing(
        val text: String,
        override val realTimestamp: Long,
        override val orderTimestamp: Long,
    ) : ChatMessage(affectsUnreadCounter = true) {
        @Composable
        override fun getDisplayText() = text
    }

    data class ParticipantJoined(
        val participant: Participant,
        override val realTimestamp: Long = System.currentTimeMillis(),
    ) : ChatMessage() {
        override val orderTimestamp = realTimestamp

        @Composable
        override fun getDisplayText(): String {
            return stringResource(R.string.ChatMessage_ParticipantJoined, participant.name)
        }
    }

    data class ParticipantLeft(
        val participant: Participant,
        override val realTimestamp: Long = System.currentTimeMillis(),
    ) : ChatMessage() {
        override val orderTimestamp = realTimestamp

        @Composable
        override fun getDisplayText(): String {
            return stringResource(R.string.ChatMessage_ParticipantLeft, participant.name)
        }
    }

    data class ScreenShareStarted(
        val share: RemoteScreenShare,
        override val realTimestamp: Long = System.currentTimeMillis(),
    ) : ChatMessage() {
        override val orderTimestamp = realTimestamp

        @Composable
        override fun getDisplayText(): String {
            return stringResource(R.string.ChatMessage_ScreenShareStarted, share.participant.name)
        }
    }

    data class ScreenShareStopped(
        val share: RemoteScreenShare,
        override val realTimestamp: Long = System.currentTimeMillis(),
    ) : ChatMessage() {
        override val orderTimestamp = realTimestamp

        @Composable
        override fun getDisplayText(): String {
            return stringResource(R.string.ChatMessage_ScreenShareStopped, share.participant.name)
        }
    }
}
