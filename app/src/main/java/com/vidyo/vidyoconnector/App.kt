package com.vidyo.vidyoconnector

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow


class App : Application() {
    init {
        appContext = this
    }
    var isPipEnabled: Boolean = true

    // State Flow to observe Screen Share Permission granted by user
    var isMediaProjectionSet = MutableStateFlow<Boolean?>(null)
}

lateinit var appContext: App
    private set
