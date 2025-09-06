package ir.miare.feature.player.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ir.miare.core.designsystem.theme.MiarePreview
import ir.miare.core.designsystem.theme.MiareTheme
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.Player

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
            containerColor = MiareTheme.colors.bg.card
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MiareTheme.colors.bg.overlay
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "League: ",
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = list.league.name,
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Country: ",
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = list.league.country,
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MiareTheme.colors.bg.overlay
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rank: ",
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "" + list.league.rank,
                            color = MiareTheme.colors.textColor
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Matches: ",
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "" + list.league.totalMatches,
                            color = MiareTheme.colors.textColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MiareTheme.colors.bg.overlay
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Teams:",
                        modifier = Modifier.align(Alignment.Start),
                        color = MiareTheme.colors.textColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    list.teams.forEach { team ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MiareTheme.colors.bg.body
                            )
                        ) {
                            // Team Name
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MiareTheme.colors.bg.card),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "" + team.rank,
                                        color = MiareTheme.colors.textColor,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "" + team.name,
                                    color = MiareTheme.colors.textColor
                                )
                            }
                            // Players
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MiareTheme.colors.bg.body
                                )
                            ) {
                                team.players.forEach { player ->
                                    PlayerCard(player = player)
                                }
                            }
                        }
                    }
                }
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