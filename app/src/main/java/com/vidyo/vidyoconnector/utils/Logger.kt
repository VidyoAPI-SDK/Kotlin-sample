package com.vidyo.vidyoconnector.utils

import android.util.Log
import com.vidyo.VidyoClient.Endpoint.Endpoint

interface Loggable {
    abstract class Tag(override val logTag: String) : Loggable

    val logTag: String
}

enum class LogLevel(val androidLogLevel: Int, val sdkLogLevel: Endpoint.ClientAppLogLevel) {
    Debug(
        androidLogLevel = Log.DEBUG,
        sdkLogLevel = Endpoint.ClientAppLogLevel.VIDYO_CLIENTAPPLOGLEVEL_Debug
    ),
    Warning(
        androidLogLevel = Log.WARN,
        sdkLogLevel = Endpoint.ClientAppLogLevel.VIDYO_CLIENTAPPLOGLEVEL_Warning
    ),
    Info(
        androidLogLevel = Log.INFO,
        sdkLogLevel = Endpoint.ClientAppLogLevel.VIDYO_CLIENTAPPLOGLEVEL_Info
    ),
    Error(
        androidLogLevel = Log.ERROR,
        sdkLogLevel = Endpoint.ClientAppLogLevel.VIDYO_CLIENTAPPLOGLEVEL_Error
    )
}

inline fun Loggable.logD(exception: Throwable? = null, block: () -> String) {
    printLogMessage(LogLevel.Debug, exception, block)
}

inline fun Loggable.logW(exception: Throwable? = null, block: () -> String) {
    printLogMessage(LogLevel.Warning, exception, block)
}

inline fun Loggable.logI(exception: Throwable? = null, block: () -> String) {
    printLogMessage(LogLevel.Info, exception, block)
}

inline fun Loggable.logE(exception: Throwable? = null, block: () -> String) {
    printLogMessage(LogLevel.Error, exception, block)
}

inline fun Loggable.logAndThrowE(exception: Throwable, block: () -> String): Nothing {
    logE(exception, block)
    throw exception
}

inline fun Loggable.printLogMessage(level: LogLevel, exception: Throwable?, block: () -> String) {
    var message = block()
    if (exception != null) {
        message = "$message\n${exception.message}\n${Log.getStackTraceString(exception)}"
    }
    Log.println(level.androidLogLevel, logTag, message)
}
