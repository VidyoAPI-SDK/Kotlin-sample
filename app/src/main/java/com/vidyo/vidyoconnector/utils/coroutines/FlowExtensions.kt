package com.vidyo.vidyoconnector.utils.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun <T> Flow<T>.collectInScope(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.(T) -> Unit
): Job {
    return scope.async(context = context) { collect { block(it) } }
}

inline fun <T> Flow<T>.collectInScopeLatest(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.(T) -> Unit
): Job {
    return scope.async(context = context) { collectLatest { block(it) } }
}
