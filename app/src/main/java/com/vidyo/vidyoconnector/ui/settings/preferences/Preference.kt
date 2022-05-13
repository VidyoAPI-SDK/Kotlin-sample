package com.vidyo.vidyoconnector.ui.settings.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
private fun PreferencePreview() {
    Preference(
        name = "Preference",
        value = "value",
        onClick = {},
    )
}

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = name,
            fontSize = 22.sp,
            color = if (enabled) Color.Unspecified else Color.Gray,
            modifier = Modifier,
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier,
            )
        }
    }
}
