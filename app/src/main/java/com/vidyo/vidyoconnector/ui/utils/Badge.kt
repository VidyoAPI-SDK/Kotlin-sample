package com.vidyo.vidyoconnector.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Badge(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()

        AnimatedVisibility(
            visible = text.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            val colors = MaterialTheme.colors
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(colors.primary, RoundedCornerShape(percent = 100))
                    .height(20.dp)
                    .widthIn(min = 20.dp),
            ) {
                Text(
                    text = text,
                    fontSize = 12.sp,
                    color = colors.onPrimary,
                )
            }
        }
    }
}
