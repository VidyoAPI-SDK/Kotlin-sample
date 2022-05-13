package com.vidyo.vidyoconnector.ui.virtual_background

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager

@Composable
fun PreviewVirtualBackgroundDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Title(onDismissRequest) },
        buttons = { Content() },
    )
}

@Composable
private fun Title(onDismissRequest: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.virtualBackgroundDialog_chooseBackground),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.h6,
        )
        IconButton(onClick = onDismissRequest) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "close",
                tint = MaterialTheme.colors.onSurface,
            )
        }
    }
}

@Composable
private fun Content() {
    val connector = LocalConnectorManager.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        val dialog = rememberSaveable { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(16.dp))
        connector.PreviewView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { dialog.value = true }
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.virtualBackgroundDialog_background),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )

            val effect = connector.virtualBackground.effect.collectAsState()
            Image(
                painter = effect.value.previewPainter().value,
                contentDescription = "preview",
                modifier = Modifier.size(54.dp, 36.dp)
            )
        }

        if (dialog.value) {
            SelectVirtualBackgroundDialog { dialog.value = false }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
