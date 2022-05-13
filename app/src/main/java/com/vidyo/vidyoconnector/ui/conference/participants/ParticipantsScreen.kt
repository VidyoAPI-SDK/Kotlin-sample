package com.vidyo.vidyoconnector.ui.conference.participants

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.ui.utils.LocalConnectorManager
import com.vidyo.vidyoconnector.ui.utils.NavBackIcon
import dev.matrix.compose_routes.ComposableRoute

@Composable
@ComposableRoute
fun ParticipantsScreen() {
    Scaffold(
        topBar = { AppBar() },
        backgroundColor = Color(0xff353535),
        contentColor = Color.White,
    ) {
        val participants = LocalConnectorManager.current.participants.all.collectAsState()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val values = participants.value
            items(values.size, key = { values[it].id }) {
                ParticipantItem(
                    participant = values[it],
//                    modifier = Modifier.animateItemPlacement(), TODO
                )
            }
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = {
            val participants = LocalConnectorManager.current.participants.all.collectAsState()
            Text(text = stringResource(R.string.participants_title, participants.value.size))
        },
        navigationIcon = { NavBackIcon() },
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
    )
}
