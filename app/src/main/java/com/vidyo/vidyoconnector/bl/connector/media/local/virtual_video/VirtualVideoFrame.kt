package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import com.vidyo.VidyoClient.Device.VideoFrame

class VirtualVideoFrame(
    val width: Int,
    val height: Int,
    val data: ByteArray,
    val format: VirtualVideoFormat,
) {
    fun toVideoFrame(): VideoFrame {
        return VideoFrame(format.sdkValue, data, data.size, width, height)
    }
}
