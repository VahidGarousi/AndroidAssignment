package ir.miare.androidcodechallenge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.miare.feature.player.presentation.navigation.PlayerBaseRoute
import ir.miare.feature.player.presentation.navigation.footballPlayerNavigation


@Composable
fun MiareNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PlayerBaseRoute
    ) {
        footballPlayerNavigation()
    }
}
