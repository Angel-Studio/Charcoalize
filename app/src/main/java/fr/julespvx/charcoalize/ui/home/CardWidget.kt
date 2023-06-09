package fr.julespvx.charcoalize.ui.home

import androidx.compose.runtime.Composable
import fr.julespvx.charcoalize.ui.home.widgets.AirQualityWidget
import fr.julespvx.charcoalize.ui.home.widgets.MoonWidget

data class CardWidget(
    val content: @Composable () -> Unit = { },
)

val cardWidgets = listOf(
    AirQualityWidget,
    MoonWidget,
)