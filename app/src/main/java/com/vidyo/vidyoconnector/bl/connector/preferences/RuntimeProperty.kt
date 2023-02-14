package com.vidyo.vidyoconnector.bl.connector.preferences

import kotlinx.coroutines.flow.MutableStateFlow

class RuntimeProperty<T> private constructor(
    private val delegate: MutableStateFlow<T>,
    private val onChange: (T) -> Unit,
) : MutableStateFlow<T> by delegate {

    constructor(initial: T, onChange: (T) -> Unit) : this(
        delegate = MutableStateFlow(initial),
        onChange = onChange,
    )

    override var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value
            onChange(value)
        }

    override fun compareAndSet(expect: T, update: T): Boolean {
        val result = delegate.compareAndSet(expect, update)
        if (result) {
            onChange(update)
        }
        return result
    }
}
