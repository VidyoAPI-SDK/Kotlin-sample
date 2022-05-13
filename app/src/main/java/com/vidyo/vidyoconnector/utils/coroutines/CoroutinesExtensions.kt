package com.vidyo.vidyoconnector.utils.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext

suspend fun invokeOnCompletion(block: (Throwable?) -> Unit) {
    coroutineContext[Job]?.invokeOnCompletion { block(it) }
}

fun CoroutineScope.invokeOnCompletion(block: (Throwable?) -> Unit) {
    coroutineContext[Job]?.invokeOnCompletion { block(it) }
}
