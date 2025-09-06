package ir.miare.androidcodechallenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ir.miare.androidcodechallenge.navigation.TopLevelDestination
import ir.miare.feature.player.presentation.navigation.navigateToLeagues
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.reflect.KClass

class MiareAppState(
    internal val navController: NavHostController
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    val topLevelDestinations: ImmutableList<TopLevelDestination> =
        TopLevelDestination.entries.toImmutableList()

    fun navigateToTopLevelDestination(
        topLevelDestination: TopLevelDestination,
    ) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
            when (topLevelDestination) {
                TopLevelDestination.LEAGUES -> navController.navigateToLeagues(navOptions = topLevelNavOptions)
            }
        }
    }

    val isShowingTopLevelDestination: Boolean
        @Composable get() {
            return topLevelDestinations.any { topLevelDestination ->
                currentDestination.isRouteInHierarchy(topLevelDestination.baseRoute)
            }
        }

}


@Composable
fun rememberMiareAppState(
    navController: NavHostController = rememberNavController(),
): MiareAppState {
    return remember(navController) {
        MiareAppState(
            navController = navController,
        )
    }
}

internal fun NavDestination?.isRouteInHierarchy(
    route: KClass<*>,
) = this?.hierarchy?.any {
    it.hasRoute(route)
} ?: false