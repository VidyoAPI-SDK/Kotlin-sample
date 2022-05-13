package com.vidyo.vidyoconnector.utils.coroutines

import kotlinx.coroutines.flow.MutableStateFlow

fun MutableStateFlow<Long>.trigger() {
    ++value
}
