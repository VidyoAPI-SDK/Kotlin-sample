package com.vidyo.vidyoconnector.bl.connector.media.local.screen_share

import com.vidyo.vidyoconnector.bl.android.IncomingCallState
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceManager
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceState
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoFrame
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoFrameRate
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoManager
import com.vidyo.vidyoconnector.bl.connector.media.local.virtual_video.VirtualVideoType
import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.logD
import com.vidyo.vidyoconnector.utils.logE
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LocalScreenShareManager(
    private val scope: ConnectorScope,
    private val conference: ConferenceManager,
    private val virtualVideo: VirtualVideoManager,
) {
    companion object : Loggable.Tag("VirtualVideoStream")

    private var sessionJob: Job = Job()
    private val activeState = MutableStateFlow(false)

    val active = activeState.asStateFlow()

    fun start(source: Flow<VirtualVideoFrame>, frameRate: VirtualVideoFrameRate) {
        logD { "start: frameRate = $frameRate" }

        sessionJob.cancel()
        sessionJob = scope.async {
            try {
                activeState.value = true
                worker(this, source, frameRate)
            } finally {
                activeState.value = false
                coroutineContext.cancelChildren()
            }
        }
    }

    fun stop() {
        logD { "stop" }
        sessionJob.cancel()
    }

    private suspend fun worker(
        sessionScope: CoroutineScope,
        source: Flow<VirtualVideoFrame>,
        frameRate: VirtualVideoFrameRate,
    ) {
        logD { "worker: started" }

        IncomingCallState.track(scope.context)
            .filter { it == IncomingCallState.Ringing }
            .collectInScope(sessionScope) {
                logD { "worker: ringing" }
                sessionScope.cancel()
            }

        conference.conference
            .filter { it.state != ConferenceState.Joined }
            .collectInScope(sessionScope) {
                logD { "worker: not in conference" }
                sessionScope.cancel()
            }

        val sink = virtualVideo.createStream(VirtualVideoType.ScreenShare, frameRate)

        try {
            source.collect { sink.sendFrame(it) }
        } catch (e: Exception) {
            when (e is CancellationException) {
                true -> logD { "worker: cancelled" }
                else -> logE(e) { "worker: failed" }
            }
        } finally {
            logD { "worker: finished" }
            sink.destroy()
        }
    }
}
