package com.vidyo.vidyoconnector.ui.settings.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
private fun PreferenceListPreview() {
    val state = remember { mutableStateOf(1) }
    PreferenceList(
        name = "PreferenceList",
        value = state.value,
        values = listOf(1, 2, 3, 4, 5),
        onDisplay = { "value $it" },
        onSelected = { state.value = it },
    )
}

@Composable
@Preview
private fun DialogContentPreview() {
    DialogContent(
        value = 1,
        values = listOf(1, 2, 3, 4, 5),
        onDisplay = { "value $it" },
        onSelected = { },
    )
}

@Composable
fun <T> PreferenceList(
    modifier: Modifier = Modifier,
    name: String,
    value: T,
    values: List<T>,
    enabled: Boolean = true,
    onDisplay: @Composable (T) -> String,
    onSelected: (T) -> Unit,
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
        AlertDialog(
            onDismissRequest = { dialog.value = false },
            title = { Text(text = name) },
            buttons = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DialogContent(
                        value = value,
                        values = values,
                        onDisplay = onDisplay,
                        onSelected = {
                            onSelected(it)
                            dialog.value = false
                        },
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .padding(top = 16.dp)
                            .padding(horizontal = 24.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth(),
                    ) {
                        TextButton(onClick = { dialog.value = false }) {
                            Text(text = stringResource(android.R.string.cancel))
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun <T> DialogContent(
    modifier: Modifier = Modifier,
    value: T,
    values: List<T>,
    onDisplay: @Composable (T) -> String,
    onSelected: (T) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(values.size) {
            val item = values[it]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .clickable { onSelected(item) }
                    .padding(vertical = 8.dp),
            ) {
                RadioButton(selected = item == value, onClick = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = onDisplay(item))
            }
        }
    }
}
