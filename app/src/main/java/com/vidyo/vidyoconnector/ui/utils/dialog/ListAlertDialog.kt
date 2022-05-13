package com.vidyo.vidyoconnector.ui.utils.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListAlertDialog(
    onDismissRequest: () -> Unit,
    onItemSelected: (Int) -> Unit,
    title: (@Composable () -> Unit)? = null,
    count: Int,
    item: (@Composable (Int) -> Unit),
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        buttons = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp),
                ) {
                    items(count) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .clickable { onItemSelected(index) }
                                .padding(vertical = 8.dp),
                        ) {
                            item(index)
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                }
            }
        },
    )
}
