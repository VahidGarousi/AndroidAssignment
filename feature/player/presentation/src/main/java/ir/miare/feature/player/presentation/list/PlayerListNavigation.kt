package ir.miare.feature.player.presentation.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object PlayerListRoute

internal fun NavGraphBuilder.playerList() {
    composable<PlayerListRoute> {
        PlayerListScreen()
    }
}