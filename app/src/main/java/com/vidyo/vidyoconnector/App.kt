package com.vidyo.vidyoconnector

import android.app.Application
import android.content.Context

class App : Application() {
    init {
        appContext = this
    }
}

lateinit var appContext: Context
    private set
