package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = TerracottaDark,
    secondary = WarmHoney,
    onSecondary = Color.White,
    secondaryContainer = WarmHoneyLight,
    onSecondaryContainer = WarmHoneyDark,
    tertiary = SageGreen,
    onTertiary = Color.White,
    tertiaryContainer = SageGreenLight,
    onTertiaryContainer = SageGreen,
    background = CreamLinenBg,
    onBackground = TextCharcoal,
    surface = CreamLinenSurface,
    onSurface = TextCharcoal,
    surfaceVariant = CreamLinenSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = CreamLinenBorder,
    outlineVariant = WarmHoney
)

private val DarkColorScheme = darkColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF471C14),
    onPrimaryContainer = Color(0xFFFFDAD3),
    secondary = WarmHoney,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4C3005),
    onSecondaryContainer = Color(0xFFFFDDB0),
    tertiary = SageGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1B3B2B),
    onTertiaryContainer = Color(0xFFBCE9CE),
    background = MidnightIndigoBg,
    onBackground = MidnightTextPrimary,
    surface = MidnightIndigoSurface,
    onSurface = MidnightTextPrimary,
    surfaceVariant = MidnightIndigoSurfaceVariant,
    onSurfaceVariant = MidnightTextSecondary,
    outline = MidnightIndigoBorder,
    outlineVariant = WarmHoney
)

@Composable
fun AiLetterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
