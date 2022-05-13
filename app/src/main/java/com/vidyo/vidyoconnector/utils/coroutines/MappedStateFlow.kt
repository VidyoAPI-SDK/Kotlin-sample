package com.vidyo.vidyoconnector.utils.coroutines

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

private class MappedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val mapper: (T) -> R,
) : StateFlow<R> {

    override val value: R
        get() = mapper(source.value)

    override val replayCache: List<R>
        get() = source.replayCache.map(mapper)

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<R>) {
        source.collect {
            collector.emit(mapper(it))
        }
    }
}

fun <T, R> StateFlow<T>.stateMap(mapper: (T) -> R): StateFlow<R> {
    return MappedStateFlow(this, mapper)
}
