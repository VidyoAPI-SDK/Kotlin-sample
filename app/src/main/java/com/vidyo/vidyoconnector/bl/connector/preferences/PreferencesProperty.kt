package com.vidyo.vidyoconnector.bl.connector.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

open class PreferencesProperty<T>(
    protected val preferences: SharedPreferences,
    protected val key: String,
    protected val read: SharedPreferences.(String) -> T,
    protected val write: SharedPreferences.Editor.(String, T) -> Unit,
) : StateFlow<T> {
    override var value = read(preferences, key)
        set(value) {
            field = value
            preferences.edit { write(key, value) }
        }

    override val replayCache: List<T>
        get() = emptyList()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        val channel = Channel<Unit>(Channel.CONFLATED)
        val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) channel.trySend(Unit)
        }

        preferences.registerOnSharedPreferenceChangeListener(callback)
        try {
            while (true) {
                collector.emit(value)
                channel.receive()
            }
        } finally {
            preferences.unregisterOnSharedPreferenceChangeListener(callback)
        }
    }
}
