package com.vidyo.vidyoconnector.ui

import android.content.Intent

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vidyo.vidyoconnector.bl.ProtocolHandler
import com.vidyo.vidyoconnector.bl.connector.conference.ConferenceState

import com.vidyo.vidyoconnector.utils.Loggable
import com.vidyo.vidyoconnector.utils.logD
import kotlinx.coroutines.launch
import com.vidyo.vidyoconnector.ui.pip.ACTION_PIP_CONTROL
import com.vidyo.vidyoconnector.ui.pip.EXTRA_CONTROL_TYPE

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.annotation.RequiresApi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import com.vidyo.vidyoconnector.utils.logE
import com.vidyo.vidyoconnector.R
import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import androidx.activity.trackPipAnimationHintView
import com.vidyo.vidyoconnector.appContext

class MainActivity : ComponentActivity() {
    companion object : Loggable.Tag("MainActivity")

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onNewIntent(intent)

        setContent {
            MainScreen(this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkAndObservePip()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ProtocolHandler.handle(intent)
        this.intent = intent
    }

    /**
     * Check if PIP feature is supported by device or not.
     */
    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        }else {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndObservePip(){
        if(isPipSupported) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    trackPipAnimationHintView(ConnectorManager.layout)
                }
            }
            observePipActionsAndParams()
            observeConferenceCallAction()
        }
        logD { "$logTag, checkAndApplyPip isPipSupported: $isPipSupported" }
    }

    /**
     * Add observer to observe
     * 1. PIP actions click event
     * 2. Any update related to PIP actions to update them like mic mute/un mute etc.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observePipActionsAndParams(){
        logD { "$logTag, observePipActionsAndParams"}
        pipActionTypeLiveData.observe(this) { viewModel.onPipActionReceived(it) }
        viewModel.pipParamsLiveData.observe(this) { (isMicroPhoneMute, isCameraMute) ->
            doOnPipParamsReceived(isMicroPhoneMute, isCameraMute)
        }
    }

    /**
     * Observer Conference Call State to check if user is in PIP mode and conference end then exit from PIP mode.
     */
    private fun observeConferenceCallAction(){
        ConnectorManager.conference.conference.collectInScope(lifecycleScope) {
            val isPipActive = viewModel.pipModeActive.value
            logD { "$logTag, observeConferenceCallAction Conference State: ${it.state}, isPipActive: $isPipActive" }
            if(!it.state.isActive && isPipActive == true){
                exitFromPipMode()
            }
        }
    }

    /**
    * Call from pipParams observer to enter or set in Pip Mode
    * @param isMicroPhoneMute Boolean
    * @param isCameraMute Boolean
    */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun doOnPipParamsReceived(isMicroPhoneMute: Boolean, isCameraMute: Boolean){
        viewModel.apply {
            logD { "$logTag, doOnPipParamsReceived isMicroPhoneMute: $isMicroPhoneMute, isCameraMute: $isCameraMute" }
            if(pipModeActive.value == true) {
                val pipParamsBuilder = viewModel.buildPictureInPictureParams(isMicroPhoneMute, isCameraMute)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pipParamsBuilder.setTitle(getString(R.string.app_name))
                }
                // Update PIP actions from mute-unmute state
                setPictureInPictureParams(pipParamsBuilder.build())
                logD { "$logTag, doOnPipParamsReceived, Pip params are updated" }
            }
        }
    }

    /**
     * Add Broadcast Receiver to listen PIP actions
     * @param isInPictureInPictureMode Boolean
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun doOnPictureInPictureModeChanged(isInPictureInPictureMode: Boolean){
        logD { "$logTag, onPictureInPictureModeChanged isInPictureInPictureMode: $isInPictureInPictureMode" }
        if (isInPictureInPictureMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    pipActionReceiver, IntentFilter(ACTION_PIP_CONTROL),
                    Context.RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(pipActionReceiver, IntentFilter(ACTION_PIP_CONTROL))
            }
        } else {
            unregisterPipActionReceiver()
        }
    }

    /**
     * When exit from pip, just to unregister pip action receiver and start activity reorder to front
     */
    private fun exitFromPipMode(){
        if (isPipSupported && viewModel.pipModeActive.value == true) {

            unregisterPipActionReceiver()
            apply {
                val startIntent = intent.apply {
                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    putExtra(ACTION_PIP_CONTROL, true)
                }
                startActivity(startIntent)
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        logD { "$logTag, onPictureInPictureModeChanged isInPictureInPictureMode: $isInPictureInPictureMode, isPipEnabled: ${appContext.isPipEnabled}" }
        if(appContext.isPipEnabled) {
            viewModel.onPictureInPictureModeChanged(isInPictureInPictureMode)
            doOnPictureInPictureModeChanged(isInPictureInPictureMode)
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        logD { "$logTag, onUserLeaveHint isPipEnabled: ${appContext.isPipEnabled}" }
        if(appContext.isPipEnabled) {
            handlePipOnUserLeave()
        }
    }

    /**
     * When user press device HOME button -> Check if PIP supported then enter users to PIP mode if conference is currently running
     */
    private fun handlePipOnUserLeave(){
        if (!isPipSupported)
            return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val confState = viewModel.getConfState()
            logD { "$logTag, onUserLeaveHint confState: $confState" }
            if(confState == ConferenceState.Joined){
                enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            }
        }
    }

    override fun onDestroy() {
        logD { "$logTag, onDestroy" }
        exitFromPipMode()
        super.onDestroy()
    }

    private fun unregisterPipActionReceiver(){
        logD { "$logTag, unregisterPipActionReceiver" }
        try {
            // Add Try-Catch to handle exception if receiver unregister call but not registered before. Safe side with no side effects.
            unregisterReceiver(pipActionReceiver)
        }catch (e: java.lang.IllegalArgumentException){
            logE { "$logTag, unregisterPipActionReceiver e: ${e.message}" }
        }
    }

    private val _pipActionType by lazy { MutableLiveData<Int>() }
    private val pipActionTypeLiveData : LiveData<Int> by lazy { _pipActionType }

    /**
     * Broadcast Receiver to receiver PIP action events
     */
    private val pipActionReceiver = object: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != ACTION_PIP_CONTROL) {
                return
            }
            val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
            logD { "$logTag, PipActionReceiver onReceive: controlType: $controlType" }
            _pipActionType.postValue(controlType)
        }
    }
}
