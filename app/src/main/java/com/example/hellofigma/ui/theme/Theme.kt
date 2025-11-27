package com.example.hellofigma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

// Dark color palette
private val DarkColorPalette = darkColors(
    primary = Primary,             // Buttons, logos, main UI
    primaryVariant = GrayText,     // Slightly darker for variants
    secondary = LinkBlue,          // Links
    background = LightGray,        // App background
    surface = White,               // Card or sheet surfaces
    onPrimary = White,             // Text on primary elements
    onSecondary = White,           // Text on secondary elements
    onBackground = GrayText,       // Text on background
    onSurface = GrayText           // Text on surfaces
)

// Light color palette
private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = GrayText,
    secondary = LinkBlue,
    background = LightGray,
    surface = White,
    onPrimary = White,
    onSecondary = GrayText,
    onBackground = GrayText,
    onSurface = GrayText
)

@Composable
fun HelloFigmaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
