package com.vidyo.vidyoconnector.ui.settings.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
private fun PreferenceTogglePreview() {
    val state = remember { mutableStateOf(false) }
    PreferenceSwitch(
        name = "PreferenceToggle",
        value = state.value,
        onChanged = { state.value = it },
    )
}

@Composable
fun PreferenceSwitch(
    modifier: Modifier = Modifier,
    name: String,
    value: Boolean,
    enabled: Boolean = true,
    onChanged: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(enabled = enabled) { onChanged(!value) }
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            color = if (enabled) Color.Unspecified else Color.Gray,
            modifier = Modifier
                .weight(weight = 1f)
                .padding(end = 8.dp),
        )
        Switch(
            enabled = enabled,
            checked = value,
            onCheckedChange = null,
        )
    }
}
