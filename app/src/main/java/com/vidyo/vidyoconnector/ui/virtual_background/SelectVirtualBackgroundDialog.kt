package com.vidyo.vidyoconnector.ui.virtual_background

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.AlertDialog
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.virtual_background.VirtualBackgroundEffect
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon

@Composable
fun SelectVirtualBackgroundDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            Column {
                Title(onDismissRequest)
                Content(onDismissRequest)
            }
        },
    )
}

@Composable
private fun Title(onDismissRequest: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
    ) {
        NavBackIcon {
            onDismissRequest()
        }
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = stringResource(R.string.virtualBackgroundDialog_background),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.h6,
        )
    }
}

@Composable
private fun Content(onDismissRequest: () -> Unit) {
    val manager = LocalConnectorManager.current.virtualBackground
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            manager.setEffect(VirtualBackgroundEffect.UriImage(it))
            onDismissRequest()
        }
    }

    LazyVerticalGrid(
        cells = GridCells.Adaptive(128.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        item {
            ContentItem(item = VirtualBackgroundEffect.None) {
                manager.setEffect(VirtualBackgroundEffect.None)
                onDismissRequest()
            }
        }

        item {
            ContentItem(item = VirtualBackgroundEffect.UriImage.Preview) {
                gallery.launch("image/*")
            }
        }

        item {
            ContentItem(item = VirtualBackgroundEffect.Blur) {
                manager.setEffect(VirtualBackgroundEffect.Blur)
                onDismissRequest()
            }
        }

        val all = VirtualBackgroundEffect.AssetImage.All
        items(all.size) {
            val item = all[it]
            ContentItem(item = item) {
                manager.setEffect(item)
                onDismissRequest()
            }
        }
    }
}

@Composable
private fun ContentItem(
    modifier: Modifier = Modifier,
    item: VirtualBackgroundEffect,
    onClick: () -> Unit,
) {
    val manager = LocalConnectorManager.current.virtualBackground
    val selected = manager.effect.collectAsState()

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(16f / 9f)
            .clickable { onClick() },
    ) {
        val painter = item.previewPainter().value
        Crossfade(targetState = painter) {
            Image(
                painter = it,
                contentDescription = item.toString(),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Text(
            text = stringResource(item.textId),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center),
            style = LocalTextStyle.current.copy(
                color = Color.Black,
                shadow = Shadow(color = Color.White, blurRadius = 4f)
            )
        )

        AnimatedVisibility(
            visible = item == selected.value,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Image(
                painter = painterResource(R.drawable.virtual_bg_dialog_chosen),
                contentDescription = item.toString(),
                modifier = Modifier
                    .padding(4.dp)
                    .size(18.dp)
                    .align(Alignment.TopEnd),
            )
        }
    }
}
