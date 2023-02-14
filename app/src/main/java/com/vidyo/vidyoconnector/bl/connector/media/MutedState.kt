package com.vidyo.vidyoconnector.bl.connector.media

enum class MutedState {
    None,
    Muted,
    ForceMuted;

    val muted: Boolean
        get() = this != None

    val forced: Boolean
        get() = this == ForceMuted
}
