package ir.miare.feature.player.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import ir.miare.feature.player.presentation.details.playerDetails
import ir.miare.feature.player.presentation.list.PlayerListRoute
import ir.miare.feature.player.presentation.list.playerList
import kotlinx.serialization.Serializable

@Serializable
data object PlayerBaseRoute


fun NavGraphBuilder.player() {
    navigation<PlayerBaseRoute>(
        startDestination = PlayerListRoute
    ) {
        playerList()
        playerDetails()
    }
}



