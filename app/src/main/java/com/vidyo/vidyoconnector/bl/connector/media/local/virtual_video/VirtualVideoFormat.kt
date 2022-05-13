package com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video

import com.vidyo.VidyoClient.Endpoint.MediaFormat

enum class VirtualVideoFormat(val sdkValue: MediaFormat) {
    RGBA(MediaFormat.VIDYO_MEDIAFORMAT_RGBA),
}
