package org.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F7BF6),
    onPrimary = Color.White,
    secondary = Color(0xFF50C8FF),
    background = Color(0xFFF4F6FB),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF161A23),
    onSurface = Color(0xFF161A23),
    onSurfaceVariant = Color(0xFF6A7385),
    primaryContainer = Color(0xFFDCEBFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7CB4FF),
    onPrimary = Color(0xFF052F6B),
    secondary = Color(0xFF50C8FF),
    background = Color(0xFF0E1118),
    surface = Color(0xFF171C27),
    onBackground = Color(0xFFF1F4FA),
    onSurface = Color(0xFFF1F4FA),
    onSurfaceVariant = Color(0xFFABB7CC),
    primaryContainer = Color(0xFF1E3A66)
)

@Composable
fun ProfileAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
