package com.vidyo.vidyoconnector.ui.virtual_background

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.bl.connector.virtual_background.VirtualBackgroundEffect
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun VirtualBackgroundScreen() {
    val orientation = LocalConfiguration.current.orientation

    Scaffold(topBar = { AppBar() }) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                Preview(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
                EffectsList(modifier = Modifier.weight(1f))
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                Preview(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                )
                EffectsList(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.virtualBackgroundDialog_chooseBackground)) },
        navigationIcon = { NavBackIcon() },
        backgroundColor = MaterialTheme.colors.surface,
    )
}

@Composable
private fun Preview(modifier: Modifier) {
    LocalConnectorManager.current.PreviewView(modifier = modifier.fillMaxWidth())
}

@Composable
private fun EffectsList(modifier: Modifier) {
    val manager = LocalConnectorManager.current.virtualBackground
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            manager.setEffect(VirtualBackgroundEffect.UriImage(it))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        modifier = modifier.padding(16.dp),
    ) {
        item {
            EffectItem(item = VirtualBackgroundEffect.None) {
                manager.setEffect(VirtualBackgroundEffect.None)
            }
        }

        item {
            EffectItem(item = VirtualBackgroundEffect.UriImage.Preview) {
                gallery.launch("image/*")
            }
        }

        item {
            EffectItem(item = VirtualBackgroundEffect.Blur) {
                manager.setEffect(VirtualBackgroundEffect.Blur)
            }
        }

        val all = VirtualBackgroundEffect.AssetImage.All
        items(all.size) {
            val item = all[it]
            EffectItem(item = item) {
                manager.setEffect(item)
            }
        }
    }
}

@Composable
private fun EffectItem(
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
