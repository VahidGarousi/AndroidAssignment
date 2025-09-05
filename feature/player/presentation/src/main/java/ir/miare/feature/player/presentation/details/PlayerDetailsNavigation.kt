package ir.miare.feature.player.presentation.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object PlayerDetailRoute

internal fun NavGraphBuilder.playerDetails() {
    composable<PlayerDetailRoute> {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Player Details")
        }
    }
}
