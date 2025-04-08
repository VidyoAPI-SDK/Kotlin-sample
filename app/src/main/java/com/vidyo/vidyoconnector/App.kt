package com.vidyo.vidyoconnector

import android.app.Application

class App : Application() {
    init {
        appContext = this
    }
    var isPipEnabled: Boolean = true
}

lateinit var appContext: App
    private set
