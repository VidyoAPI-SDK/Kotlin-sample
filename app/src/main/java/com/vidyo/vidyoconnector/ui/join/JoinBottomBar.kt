package com.vidyo.vidyoconnector.ui.join

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vidyo.vidyoconnector.ui.conference.icons.*

@Composable
fun JoinBottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .background(color = Color.Black)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .requiredWidthIn(max = 320.dp),
        ) {
            val defaultModifier = Modifier
                .fillMaxHeight()
                .aspectRatio(ratio = 1f)

            SettingsIcon(modifier = defaultModifier)
            Spacer(modifier = defaultModifier)
            SpeakerPrivacyIcon(modifier = defaultModifier)
            MicrophonePrivacyIcon(modifier = defaultModifier)
            CameraPrivacyIcon(modifier = defaultModifier)
            Spacer(modifier = defaultModifier)
            CameraEffectIcon(modifier = defaultModifier)
        }
    }
}
