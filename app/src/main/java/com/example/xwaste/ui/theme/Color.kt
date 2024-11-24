package com.example.xwaste.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your custom colors
private val CustomLightColorScheme = lightColorScheme(
    primary = Color.Black, // Primary color
    onPrimary = Color.White, // Text color on primary
    secondary = Color.Gray, // Secondary color
    onSecondary = Color.White, // Text color on secondary
    background = Color.White, // Background color
    onBackground = Color.Black, // Text color on background
    surface = Color.White, // Surface color
    onSurface = Color.Black, // Text color on surface
)

private val CustomDarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color.Gray,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
)

// Provide your custom color scheme
@Composable
fun XWasteTheme(
    darkTheme: Boolean = false, // Change to true for dark mode
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) {
        CustomDarkColorScheme
    } else {
        CustomLightColorScheme
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colors,
        typography = Typography, // Use your typography settings
        content = content
    )
}
