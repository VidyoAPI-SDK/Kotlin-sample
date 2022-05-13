package com.vidyo.vidyoconnector.ui.conference.chats

import android.text.format.DateUtils
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.vidyo.vidyoconnector.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun ChatMessageTime(timestamp: Long, modifier: Modifier = Modifier) {
    val text = remember(timestamp) { mutableStateOf("") }
    val lessThanMinuteAgo = stringResource(R.string.chat_lessThanMinuteAgo)

    LaunchedEffect(timestamp) {
        while (isActive) {
            val now = System.currentTimeMillis()
            val time = timestamp.coerceAtMost(now)

            val diff = now - time
            text.value = when (diff < DateUtils.MINUTE_IN_MILLIS) {
                true -> lessThanMinuteAgo
                else -> DateUtils.getRelativeTimeSpanString(time, now, 0).toString()
            }

            delay(DateUtils.MINUTE_IN_MILLIS)
        }
    }

    Text(
        text = text.value,
        color = Color.LightGray,
        modifier = modifier,
    )
}
