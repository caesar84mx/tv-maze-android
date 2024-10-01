package com.caesar84mx.tvmaze.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF1B3B43),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000)
)

val LightColorPalette = lightColorScheme(
    primary = Color(0xFF1B3B43),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    background = Color(0xFFF1F2F6),
    onBackground = Color(0xFF2B2B2B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2B2B2B),
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun TvMazeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}