package com.vidyo.vidyoconnector.ui

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager

import com.vidyo.vidyoconnector.utils.Loggable

import com.vidyo.vidyoconnector.utils.logD

import com.vidyo.vidyoconnector.ui.pip.CONTROL_TYPE_CAMERA_MUTE
import com.vidyo.vidyoconnector.ui.pip.CONTROL_TYPE_CAMERA_UN_MUTE
import com.vidyo.vidyoconnector.ui.pip.CONTROL_TYPE_END_CALL
import com.vidyo.vidyoconnector.ui.pip.CONTROL_TYPE_MICROPHONE_MUTE
import com.vidyo.vidyoconnector.ui.pip.CONTROL_TYPE_MICROPHONE_UN_MUTE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.vidyo.vidyoconnector.ui.pip.PipControlType
import com.vidyo.vidyoconnector.ui.pip.PipManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScopeNow
import com.vidyo.vidyoconnector.utils.coroutines.toFlow

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter

/**
 * ViewModel for MainActivity to handle PIP related actions and sync Conference state and Audio/Video states with PIP
 * @property pipModeActive MutableLiveData<(Boolean..Boolean?)>
 * @property pipManager PipManager
 * @property _pipParams MutableLiveData<Pair<Boolean, Boolean>>
 * @property pipParamsLiveData LiveData<Pair<Boolean, Boolean>>
 * @constructor
 */
class MainViewModel(context: Application) : AndroidViewModel(context) {

    companion object : Loggable.Tag("MainViewModel")

    val pipModeActive = MutableLiveData(false)
    private val pipManager = PipManager(context)

    /**
     * Get Current State of Conference.
     * @return ConferenceState
     */
    fun getConfState() = ConnectorManager.conference.conference.value.state

    private val _pipParams by lazy { MutableLiveData<Pair<Boolean, Boolean>>() }
    val pipParamsLiveData : LiveData<Pair<Boolean, Boolean>> by lazy { _pipParams }

    init{
        /** PIP Implementation **/
        combine(ConnectorManager.media.localMicrophone.muted, ConnectorManager.media.localCamera.muted,
            pipModeActive.toFlow(), ::Triple)
            .filter { it.third == true }
            .collectInScopeNow(viewModelScope) {
                logD { "$logTag, initBuildPipParams: micMutedState = ${it.first.muted}, cameraMutedState = ${it.second.muted}" }
                _pipParams.postValue(Pair(it.first.muted, it.second.muted))
            }
        /** PIP Implementation **/
    }

    /**
     * To observe Pip Actions from pipActionReceiver -> pipActionTypeLiveData
     * @param controlType Int
     */
    fun onPipActionReceived(@PipControlType controlType: Int){
        logD { "$logTag, onPipActionReceived controlType: $controlType" }
        when(controlType){
            CONTROL_TYPE_MICROPHONE_MUTE, CONTROL_TYPE_MICROPHONE_UN_MUTE -> {
                ConnectorManager.media.localMicrophone.requestMutedState(!ConnectorManager.media.localMicrophone.muted.value.muted)
            }

            CONTROL_TYPE_CAMERA_UN_MUTE, CONTROL_TYPE_CAMERA_MUTE -> {
                ConnectorManager.media.localCamera.requestMutedState(!ConnectorManager.media.localCamera.muted.value.muted)
            }

            CONTROL_TYPE_END_CALL ->
                ConnectorManager.conference.disconnect()
        }
    }

    /**
     * Trigger when Pip Mode changed from activity or fragment.
     * @param isInPictureInPictureMode Boolean
     */
    fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        logD { "$logTag, onPictureInPictureModeChanged isInPictureInPictureMode: $isInPictureInPictureMode" }
        // Pip Current Status Live Data
        pipModeActive.value = isInPictureInPictureMode
        ConnectorManager.updatePipActiveState(isInPictureInPictureMode)
    }

    /**
     *
     * @param isMicroPhoneMute Boolean
     * @param isCameraMute Boolean
     * @return Builder
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildPictureInPictureParams(isMicroPhoneMute: Boolean, isCameraMute: Boolean) = pipManager.buildPictureInPictureParams(isMicroPhoneMute, isCameraMute)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDefaultPipParams() = pipManager.getDefaultPipParams()
}
