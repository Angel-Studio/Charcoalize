package fr.julespvx.charcoalize.ui.theme.themes

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

interface Theme {
    val lightColorScheme: ColorScheme
    val darkColorScheme: ColorScheme

    val seed: Color

    fun colorScheme(isDark: Boolean): ColorScheme = if (isDark) darkColorScheme else lightColorScheme
}