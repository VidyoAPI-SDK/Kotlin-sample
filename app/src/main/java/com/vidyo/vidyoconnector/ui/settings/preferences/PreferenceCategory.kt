package com.vidyo.vidyoconnector.ui.settings.preferences

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
private fun PreferenceCategoryPreview(modifier: Modifier = Modifier) {
    PreferenceCategory(name = "PreferenceCategory", modifier = modifier)
}

@Composable
fun PreferenceCategory(modifier: Modifier = Modifier, @StringRes name: Int) {
    PreferenceCategory(
        modifier = modifier,
        name = stringResource(name),
    )
}

@Composable
fun PreferenceCategory(modifier: Modifier = Modifier, name: String) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = name.uppercase(),
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp),
        )
        Divider()
    }
}
