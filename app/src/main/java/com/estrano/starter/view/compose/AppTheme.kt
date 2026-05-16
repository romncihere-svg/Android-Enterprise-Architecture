package com.estrano.starter.view.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF03DAC5),
    secondary = Color(0xFF03DAC5),
    tertiary = Color(0xFF03DAC5),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun EstranoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Force dark for premium feel

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
