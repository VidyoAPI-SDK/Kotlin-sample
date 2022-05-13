package com.vidyo.vidyoconnector.bl.connector.preferences

import android.content.SharedPreferences
import androidx.core.content.edit

class ConnectorProperty<T>(
    preferences: SharedPreferences,
    key: String,
    read: SharedPreferences.(String) -> T,
    write: SharedPreferences.Editor.(String, T) -> Unit,
    private val get: (T) -> T,
    private val set: (T) -> Boolean,
) : PreferencesProperty<T>(
    preferences = preferences,
    key = key,
    read = read,
    write = write,
) {
    override var value = getInitialValue()
        set(value) {
            if (set(value)) {
                field = value
                preferences.edit { write(key, get(value)) }
            }
        }

    private fun getInitialValue(): T {
        val stored = read(preferences, key)
        val updated = set(stored)
        val changed = get(stored)
        if (!updated || changed != stored) {
            preferences.edit { write(key, changed) }
        }
        return changed
    }
}
