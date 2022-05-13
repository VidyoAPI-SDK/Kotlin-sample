package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import kotlin.time.Duration.Companion.seconds

enum class VirtualVideoFrameRate(val fpsMin: Int, val fpsMax: Int) {
    Normal(fpsMin = 5, fpsMax = 9),
    High(fpsMin = 30, fpsMax = 30);

    companion object {
        val Default = Normal
    }

    val intervalMax = 1.seconds / fpsMin
    val intervalMin = 1.seconds / fpsMax
}
