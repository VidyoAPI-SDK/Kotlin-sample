package com.vidyo.vidyoconnector.ui.settings.preferences

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun PreferenceTextFieldPreview() {
    val state = remember { mutableStateOf("value") }
    PreferenceTextField(
        name = "PreferenceTextField",
        value = state.value,
        onChanged = { state.value = it },
    )
}

@Composable
fun PreferenceTextField(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onDisplay: (String) -> String = { it },
    onChanged: (String) -> Unit,
) {
    val dialog = rememberSaveable { mutableStateOf(false) }

    Preference(
        modifier = modifier,
        name = name,
        value = onDisplay(value),
        enabled = enabled,
        onClick = { dialog.value = true },
    )

    if (dialog.value) {
        val text = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(value, TextRange(value.length)))
        }
        AlertDialog(
            onDismissRequest = { dialog.value = false },
            title = { Text(text = name) },
            text = {
                val focusRequester = remember { FocusRequester() }
                val style = LocalTextStyle.current.copy(
                    color = MaterialTheme.colors.onSurface,
                )

                BasicTextField(
                    keyboardOptions = keyboardOptions,
                    textStyle = style,
                    value = text.value,
                    onValueChange = { text.value = it },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                )

                LaunchedEffect("focus") {
                    focusRequester.requestFocus()
                }
            },
            dismissButton = {
                TextButton(onClick = { dialog.value = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onChanged(text.value.text)
                        dialog.value = false
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }
}
