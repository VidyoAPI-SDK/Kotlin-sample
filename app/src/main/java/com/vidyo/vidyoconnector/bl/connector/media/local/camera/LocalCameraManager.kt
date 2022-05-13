package com.vidyo.vidyoconnector.bl.connector.media.local.camera

import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Device.Device
import com.vidyo.vidyoconnector.bl.connector.ConnectorScope
import com.vidyo.vidyoconnector.bl.connector.preferences.PreferencesManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.coroutines.trigger
import kotlinx.coroutines.flow.*
import com.vidyo.VidyoClient.Device.LocalCamera as VcLocalCamera

class LocalCameraManager(private val scope: ConnectorScope, preferences: PreferencesManager) {
    private val map = HashMap<String, LocalCamera>()
    private val mapTrigger = MutableStateFlow(0L)
    private val allState = MutableStateFlow(emptyList<LocalCamera>())
    private val mutedState = MutableStateFlow(false)
    private val selectedState = MutableStateFlow<LocalCamera?>(null)

    val all = allState.asStateFlow()
    val muted = mutedState.asStateFlow()
    val selected = selectedState.asStateFlow()

    init {
        scope.connector.registerLocalCameraEventListener(EventListener())
        scope.connector.selectDefaultCamera()

        mapTrigger.debounce(500).collectInScope(scope) {
            val temp = map.values.toMutableList()
            temp.sortBy { it.name }
            allState.value = temp
        }

        combine(
            selected.filterNotNull(),
            preferences.localCameraConstraints.filterNotNull(),
        ) { camera, constraints ->
            val supported = when (camera.constraints.contains(constraints)) {
                true -> camera.handle.setMaxConstraint(
                    constraints.width,
                    constraints.height,
                    constraints.frameInterval.inWholeNanoseconds,
                )
                else -> false
            }
            if (!supported) {
                preferences.localCameraConstraints.value = null
            }
        }.launchIn(scope)
    }

    fun selectDevice(device: LocalCamera) {
        scope.connector.selectLocalCamera(device.handle)
    }

    fun requestMutedState(muted: Boolean) {
        if (scope.connector.setCameraPrivacy(muted)) {
            mutedState.value = muted
        }
    }

    private inner class EventListener : Connector.IRegisterLocalCameraEventListener {
        override fun onLocalCameraAdded(localCamera: VcLocalCamera) {
            scope.run {
                val device = LocalCamera.from(localCamera)
                map[device.id] = device
                mapTrigger.trigger()
            }
        }

        override fun onLocalCameraRemoved(localCamera: VcLocalCamera) {
            scope.run {
                map.remove(localCamera.id.orEmpty())
                mapTrigger.trigger()
            }
        }

        override fun onLocalCameraSelected(localCamera: VcLocalCamera?) {
            scope.run {
                selectedState.value = when (localCamera == null) {
                    true -> null
                    else -> map[localCamera.id]
                }
            }
        }

        override fun onLocalCameraStateUpdated(localCamera: VcLocalCamera, state: Device.DeviceState?) {
            val deviceId = localCamera.id.orEmpty()
            val device = map[deviceId] ?: return

            scope.run {
                when (state) {
                    Device.DeviceState.VIDYO_DEVICESTATE_Controllable,
                    Device.DeviceState.VIDYO_DEVICESTATE_NotControllable -> {
                        device.updateControlCapabilities()
                    }
                    else -> Unit
                }
            }
        }
    }
}
