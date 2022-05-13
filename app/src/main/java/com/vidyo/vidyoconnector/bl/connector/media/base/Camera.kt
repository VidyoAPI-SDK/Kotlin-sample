package com.vidyo.vidyoconnector.bl.connector.media.base

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

interface Camera {
    val id: String
    val name: String
    val participantId: String
    val controlCapabilities: StateFlow<CameraControlCapabilities>

    fun controlPtzNudge(action: CameraControlAction)
    fun controlPtzContinuousStart(action: CameraControlAction, timeout: Long)
    fun controlPtzContinuousStop(action: CameraControlAction)

    suspend fun controlPtzContinuous(action: CameraControlAction) {
        val delay = 100.milliseconds
        val timeout = delay.inWholeNanoseconds * 3

        try {
            while (true) {
                controlPtzContinuousStart(action, timeout)
                delay(delay)
            }
        } finally {
            controlPtzContinuousStop(action)
        }
    }
}
