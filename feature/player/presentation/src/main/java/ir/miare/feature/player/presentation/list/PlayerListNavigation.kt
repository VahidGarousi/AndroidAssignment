package ir.miare.feature.player.presentation.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object PlayerListRoute

internal fun NavGraphBuilder.playerList() {
    composable<PlayerListRoute> {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Player Screen")
        }
    }
}