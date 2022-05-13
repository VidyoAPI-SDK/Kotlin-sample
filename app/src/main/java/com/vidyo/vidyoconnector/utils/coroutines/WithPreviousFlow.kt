package com.vidyo.vidyoconnector.utils.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

data class WithPrevious<T>(
    val old: T,
    val new: T,
)

fun <T> Flow<T>.withPrevious(initial: T) = flow {
    var previous = initial
    collect {
        emit(WithPrevious(old = previous, new = it))
        previous = it
    }
}
