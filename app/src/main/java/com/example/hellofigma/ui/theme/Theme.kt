package com.example.hellofigma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

// Dark color palette
private val DarkColorPalette = darkColors(
    primary = Primary,             // Buttons, logos, main UI
    primaryVariant = PurpleLight,  // Slightly darker/lighter for variants (from Color.kt)
    secondary = LinkBlue,          // Links
    background = LightGray,        // App background
    surface = White,               // Card or sheet surfaces
    error = TrueRed,               // Errors/alerts (from Color.kt)
    onPrimary = White,             // Text on primary elements
    onSecondary = White,           // Text on secondary elements
    onBackground = TextSecondary,  // Text on background (from Color.kt)
    onSurface = TextSecondary      // Text on surfaces (from Color.kt)
)

// Light color palette
private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = PurpleLight,  // Variants (from Color.kt)
    secondary = LinkBlue,
    background = LightGray,
    surface = White,
    error = TrueRed,               // Errors (from Color.kt)
    onPrimary = White,
    onSecondary = GrayText,
    onBackground = TextSecondary,  // Better text contrast (from Color.kt)
    onSurface = TextSecondary      // Better text contrast (from Color.kt)
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