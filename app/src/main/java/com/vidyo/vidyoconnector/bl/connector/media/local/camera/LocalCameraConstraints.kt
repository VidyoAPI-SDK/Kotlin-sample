package com.vidyo.vidyoconnector.bl.connector.media.local.camera

import com.vidyo.VidyoClient.Device.VideoCapability
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

data class LocalCameraConstraints(
    val width: Int,
    val height: Int,
    val frameInterval: Duration,
) {
    companion object {
        fun from(camera: LocalCamera): List<LocalCameraConstraints> {
            val capabilities = ArrayList<VideoCapability>()
            camera.handle.getVideoCapabilities(capabilities)

            val set = HashSet<LocalCameraConstraints>()
            for (capability in capabilities) {
                for (range in capability.ranges) {
                    set.add(
                        LocalCameraConstraints(
                            width = capability.width.toInt(),
                            height = capability.height.toInt(),
                            frameInterval = range.range.begin.nanoseconds,
                        )
                    )
                    set.add(
                        LocalCameraConstraints(
                            width = capability.width.toInt(),
                            height = capability.height.toInt(),
                            frameInterval = range.range.end.nanoseconds,
                        )
                    )
                }
            }

            return set.sortedWith(compareBy({ it.width }, { it.height }, { it.fps }))
        }
    }

    val fps = computeApproximateFps(frameInterval)
}

private fun computeApproximateFps(frameInterval: Duration): Int {
    return (1.seconds / frameInterval).roundToInt()
}

fun List<LocalCameraConstraints>.distinctBySizes(): List<LocalCameraConstraints> {
    return distinctBy {
        (it.width.toLong() shl 32) or it.height.toLong()
    }
}

fun List<LocalCameraConstraints>.distinctByFrameIntervals(): List<LocalCameraConstraints> {
    return distinctBy {
        it.frameInterval
    }
}
