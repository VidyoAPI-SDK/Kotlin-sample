package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import kotlin.time.Duration.Companion.seconds

enum class VirtualVideoFrameRate(val fpsMax: Int) {
    Normal(fpsMax = 6),
    High(fpsMax = 30);

    companion object {
        val Default = Normal
    }

    val intervalMin = 1.seconds / fpsMax
}
