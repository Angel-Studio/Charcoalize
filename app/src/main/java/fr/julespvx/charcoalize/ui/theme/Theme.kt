package fr.julespvx.charcoalize.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import fr.julespvx.charcoalize.ui.theme.themes.CharcoalTheme


@Composable
fun CharcoalizeTheme(
    darkTheme: Boolean = /*isSystemInDarkTheme()*/ true, // Always dark theme
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = CharcoalTheme().colorScheme(isDark = darkTheme),
        typography = Typography,
        content = content
    )
}