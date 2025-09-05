package ir.miare.feature.player.presentation.list.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ir.miare.core.designsystem.theme.MiarePreview
import ir.miare.core.designsystem.theme.MiareTheme
import ir.miare.feature.player.domain.model.LeagueList

@Composable
fun LeagueCard(
    modifier: Modifier = Modifier,
    list: LeagueList
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiareTheme.colors.pageBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Text(
                text = list.league.name,
                color = MiareTheme.colors.textColor
            )
            Text(
                text = list.league.country,
                color = MiareTheme.colors.textColor
            )
            Text(
                text = "" + list.league.rank,
                color = MiareTheme.colors.textColor
            )
            Text(
                text = "" + list.league.totalMatches,
                color = MiareTheme.colors.textColor
            )
            list.players.forEach { player ->
                Text(
                    text = player.name,
                    color = MiareTheme.colors.textColor
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun LeagueCardPreview(
    @PreviewParameter(LeagueListPreviewProvider::class)
    list: LeagueList
) {
    MiarePreview {
        LeagueCard(
            modifier = Modifier.fillMaxWidth(),
            list = list
        )
    }
}