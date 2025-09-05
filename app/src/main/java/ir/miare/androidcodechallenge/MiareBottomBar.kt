package ir.miare.androidcodechallenge

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import ir.miare.androidcodechallenge.navigation.TopLevelDestination
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MiareBottomBar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    destinations: ImmutableList<TopLevelDestination>,
    onNavigationSelected: (TopLevelDestination) -> Unit,
) {
    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        modifier = modifier.fillMaxWidth(),
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    onNavigationSelected(destination)
                },
                icon = {
                    Icon(
                        destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.titleTextId),
                    )
                },
            )
        }
    }
}