package fr.julespvx.charcoalize.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector

interface CharcoalizeDestination {
    val icon: ImageVector
    val route: String
    val title: String
}

object CharcoalizeHome : CharcoalizeDestination {
    override val icon = Icons.Rounded.Home
    override val route = "home"
    override val title = "Home"
}

val mainDestinations = listOf(
    CharcoalizeHome,
)