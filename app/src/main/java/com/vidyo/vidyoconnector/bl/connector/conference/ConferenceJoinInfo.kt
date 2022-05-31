package com.vidyo.vidyoconnector.bl.connector.conference

import com.vidyo.VidyoClient.Connector.Connector

sealed class ConferenceJoinInfo {

    data class AsGuest(
        val portal: String,
        val name: String,
        val roomKey: String,
        val roomPin: String,
    ) : ConferenceJoinInfo() {
        override fun join(connector: Connector, callback: Connector.IConnect): Boolean {
            return connector.connectToRoomAsGuest(
                portal,
                name.trim(),
                roomKey,
                roomPin,
                callback,
            )
        }
    }

    data class AsUser(
        val portal: String,
        val username: String,
        val password: String,
        val roomKey: String,
        val roomPin: String,
    ) : ConferenceJoinInfo() {
        override fun join(connector: Connector, callback: Connector.IConnect): Boolean {
            return connector.connectToRoomWithKey(
                portal,
                username,
                password,
                roomKey,
                roomPin,
                callback,
            )
        }
    }

    abstract fun join(connector: Connector, callback: Connector.IConnect): Boolean

}
