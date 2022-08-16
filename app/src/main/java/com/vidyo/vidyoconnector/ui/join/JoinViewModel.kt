package com.vidyo.vidyoconnector.ui.join

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import com.vidyo.vidyoconnector.BuildConfig
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceJoinInfo
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import kotlinx.parcelize.Parcelize

enum class JoinContent {
    Unknown,
    Auto,
    Guest,
    User,
}

@Parcelize
class JoinViewModelState(
    val type: JoinContent,
    val portal: String,
    val username: String,
    val password: String,
    val roomKey: String,
    val roomPin: String,
) : Parcelable

class JoinViewModel {
    object Saver : androidx.compose.runtime.saveable.Saver<JoinViewModel, JoinViewModelState> {
        override fun restore(value: JoinViewModelState) = JoinViewModel().apply {
            type.value = value.type
            portal.value = value.portal
            username.value = value.username
            password.value = value.password
            roomKey.value = value.roomKey
            roomPin.value = value.roomPin
        }

        override fun SaverScope.save(value: JoinViewModel) = JoinViewModelState(
            type = value.type.value,
            portal = value.portal.value,
            username = value.username.value,
            password = value.password.value,
            roomKey = value.roomKey.value,
            roomPin = value.roomPin.value,
        )
    }

    val type = mutableStateOf(JoinContent.Unknown)
    val portal = mutableStateOf(BuildConfig.DEFAULT_GUEST_PORTAL)
    val username = mutableStateOf(BuildConfig.DEFAULT_GUEST_NAME)
    val password = mutableStateOf("")
    val roomKey = mutableStateOf(BuildConfig.DEFAULT_GUEST_ROOM_KEY)
    val roomPin = mutableStateOf(BuildConfig.DEFAULT_GUEST_ROOM_PIN)

    fun join(manager: ConferenceManager) {
        val info = when (type.value) {
            JoinContent.Auto -> ConferenceJoinInfo.AsAuto(
                name = username.value,
            )
            JoinContent.Guest -> ConferenceJoinInfo.AsGuest(
                portal = portal.value,
                name = username.value,
                roomKey = roomKey.value,
                roomPin = roomPin.value,
            )
            JoinContent.User -> ConferenceJoinInfo.AsUser(
                portal = portal.value,
                username = username.value,
                password = password.value,
                roomKey = roomKey.value,
                roomPin = roomPin.value,
            )
            JoinContent.Unknown -> return
        }
        manager.join(info)
    }
}
