package ir.miare.feature.player.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ir.miare.core.designsystem.MiareIcons
import ir.miare.core.designsystem.theme.MiarePreview
import ir.miare.core.designsystem.theme.MiareTheme
import ir.miare.feature.player.domain.model.Player

@Composable
fun PlayerCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MiareTheme.colors.bg.card,
    player: Player
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlayerImage()
            PlayerInformation(
                modifier = Modifier.weight(1f),
                player = player
            )
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MiareTheme.colors.bg.overlay
                )
            ) {
                Icon(
                    imageVector = MiareIcons.Favorite,
                    contentDescription = null,
                    tint = MiareTheme.colors.textColor
                )
            }
        }
    }
}


@Composable
private fun PlayerInformation(
    modifier: Modifier = Modifier,
    player: Player
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Name: ",
                color = MiareTheme.colors.textColor,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = player.name,
                color = MiareTheme.colors.textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Total Goal: ",
                color = MiareTheme.colors.textColor,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "" + player.totalGoal,
                color = MiareTheme.colors.textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun PlayerImage() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MiareTheme.colors.bg.overlay)
    )
}


@PreviewLightDark
@Composable
private fun PlayerCardPreview() {
    MiarePreview {
        PlayerCard(
            player = Player(
                name = "Vahid Garousi",
                totalGoal = 60
            )
        )
    }
}