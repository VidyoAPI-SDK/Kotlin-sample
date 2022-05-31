package com.vidyo.vidyoconnector.bl.connector.conference

import android.net.Uri
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class RoomInfo(
    val pin: String = "",
    val roomUrl: String = "",
    val extension: String = "",
    val inviteContent: String = "",
)

object RoomCreationService {
    private val client = HttpClient()

    suspend fun resolveJoinInfo(info: ConferenceJoinInfo.AsAuto): Pair<RoomInfo, ConferenceJoinInfo> {
        val response = client.post("https://vidyo-adhoc-zsdgxlqgkq-uc.a.run.app/api/v1/rooms")
        val json = response.body<String>()
        val data = Json.decodeFromString<RoomInfo>(json)
        val uri = Uri.parse(data.roomUrl)

        return data to ConferenceJoinInfo.AsGuest(
            portal = uri.authority.orEmpty(),
            name = info.name,
            roomKey = uri.lastPathSegment.orEmpty(),
            roomPin = data.pin,
        )
    }
}
