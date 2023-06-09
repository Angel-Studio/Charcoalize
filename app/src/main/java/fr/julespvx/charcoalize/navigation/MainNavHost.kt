package fr.julespvx.charcoalize.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import fr.julespvx.charcoalize.ui.home.HomeScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CharcoalizeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues,
    searchBarActivated: MutableState<Boolean>,
) {
    val context = LocalContext.current

    AnimatedNavHost(
        navController = navController,
        startDestination = mainDestinations.first().route,
        modifier = modifier,
    ) {
        // Home
        composable(CharcoalizeHome.route) {
            HomeScreen(
                innerPadding = innerPadding,
                searchBarActivated = searchBarActivated,
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }