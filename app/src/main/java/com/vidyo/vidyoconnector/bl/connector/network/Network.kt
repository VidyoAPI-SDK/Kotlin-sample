package com.vidyo.vidyoconnector.bl.connector.network

import com.vidyo.VidyoClient.NetworkInterface

data class Network(
    val name: String,
    val handle: NetworkInterface?,
) {
    companion object {
        val Null = Network("", null)

        fun from(handle: NetworkInterface) = Network(
            name = handle.name,
            handle = handle,
        )
    }

    override fun hashCode(): Int {
        if (handle == null) {
            return 0
        }
        return handle.GetObjectPtr().toInt()
    }

    override fun equals(other: Any?): Boolean {
        return other is Network && other.handle?.GetObjectPtr() == handle?.GetObjectPtr()
    }
}
