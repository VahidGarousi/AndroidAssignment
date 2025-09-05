package ir.miare.feature.player.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ir.miare.feature.player.presentation.details.playerDetails
import ir.miare.feature.player.presentation.list.PlayerListRoute
import ir.miare.feature.player.presentation.list.playerList
import kotlinx.serialization.Serializable

@Serializable
data object PlayerBaseRoute  // route to base navigation graph

fun NavHostController.navigateToLeagues(
    navOptions: NavOptions? = null
) = navigate(route = PlayerBaseRoute,navOptions = navOptions)

fun NavGraphBuilder.footballPlayerNavigation() {
    navigation<PlayerBaseRoute>(
        startDestination = PlayerListRoute
    ) {
        playerList()
        playerDetails()
    }
}



