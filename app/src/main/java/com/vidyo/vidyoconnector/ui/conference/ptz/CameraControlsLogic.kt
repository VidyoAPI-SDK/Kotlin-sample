package com.vidyo.vidyoconnector.ui.conference.ptz

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.PointerInputScope
import com.vidyo.vidyoconnector.bl.connector.media.base.Camera
import com.vidyo.vidyoconnector.bl.connector.media.base.CameraControlAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

class CameraControlsLogic(val camera: Camera) {

    private val currentAction = MutableStateFlow<CameraControlAction?>(null)

    suspend fun startWorker() {
        combine(currentAction, camera.controlCapabilities) { a, b -> Pair(a, b) }.collectLatest {
            val action = it.first ?: return@collectLatest
            val capabilities = it.second

            when {
                action.hasContinuousMode(capabilities) -> {
                    camera.controlPtzContinuous(action)
                }
                action.hasNudgeMode(capabilities) -> {
                    camera.controlPtzNudge(action)
                }
            }
        }
    }

    suspend fun handleEvent(scope: PointerInputScope, action: CameraControlAction) {
        scope.awaitPointerEventScope {
            while (true) {
                try {
                    awaitFirstDown(requireUnconsumed = false)
                    currentAction.value = action
                    waitForUpOrCancellation()
                } finally {
                    if (currentAction.value == action) {
                        currentAction.value = null
                    }
                }
            }
        }
    }
}
