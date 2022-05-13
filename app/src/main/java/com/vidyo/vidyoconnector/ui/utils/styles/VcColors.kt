package com.vidyo.vidyoconnector.ui.utils.styles

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object VcColors {
    private val lightColors = lightColors(
        background = Color.White,
        onBackground = Color.Black,

        surface = Color.LightGray,
        onSurface = Color.Black,

        primary = Color(0xFF80B02F),
        primaryVariant = Color(0xFF80B02F),
        onPrimary = Color.White,

        secondary = Color(0xFF207DCD),
        secondaryVariant = Color(0xFF207DCD),
        onSecondary = Color.White,
    )

    private val darkColors = darkColors(
        background = Color.Black,
        onBackground = Color.White,

        surface = Color.DarkGray,
        onSurface = Color.White,

        primary = Color(0xFF80B02F),
        primaryVariant = Color(0xFF80B02F),
        onPrimary = Color.White,

        secondary = Color(0xFF207DCD),
        secondaryVariant = Color(0xFF207DCD),
        onSecondary = Color.White,
    )

    val colors: Colors
        @Composable
        get() = when (isSystemInDarkTheme()) {
            true -> darkColors
            else -> lightColors
        }
}
