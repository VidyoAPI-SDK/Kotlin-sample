package com.vidyo.vidyoconnector.utils.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun <T> LiveData<T>.toFlow(): Flow<T> = channelFlow {
    val observer = Observer<T> { trySend(it) }
    observeForever(observer)
    awaitClose {
        removeObserver(observer)
    }
}
