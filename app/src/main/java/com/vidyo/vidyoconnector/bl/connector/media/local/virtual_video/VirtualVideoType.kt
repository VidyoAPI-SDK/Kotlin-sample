package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import com.vidyo.VidyoClient.Device.VirtualVideoSource.VirtualVideoSourceType
import kotlin.math.min

enum class VirtualVideoType(val jniValue: VirtualVideoSourceType) {
    Camera(
        jniValue = VirtualVideoSourceType.VIDYO_VIRTUALVIDEOSOURCETYPE_CAMERA,
    ),
    ScreenShare(
        jniValue = VirtualVideoSourceType.VIDYO_VIRTUALVIDEOSOURCETYPE_SHARE,
    );

    companion object {
        private const val MAX_SMALLER_RESOLUTION = 1080

        fun fromJniValue(value: VirtualVideoSourceType): VirtualVideoType {
            return values().find { it.jniValue == value } ?: ScreenShare
        }
    }

    fun computeScaleMax(width: Int, height: Int): Float {
        return MAX_SMALLER_RESOLUTION.toFloat() / min(width, height).coerceAtLeast(
            MAX_SMALLER_RESOLUTION
        )
    }

    fun computeScaleMin(width: Int, height: Int): Float {
        return computeScaleMax(width, height) / 2
    }
}
