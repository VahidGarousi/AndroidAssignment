package ir.miare.feature.player.presentation.list.component

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team

class LeagueListPreviewProvider : PreviewParameterProvider<LeagueList> {
    override val values = sequenceOf(
        LeagueList(
            league = League("Spain", "La Liga", 1, 38),
            players = listOf(
                Player("Jude Bellingham", Team("Real Madrid", 1), 19),
                Player("Vinicius Junior", Team("Real Madrid", 1), 15)
            )
        ),
        LeagueList(
            league = League("England", "Premier League", 2, 38),
            players = listOf(
                Player("Harry Kane", Team("Tottenham", 3), 18),
                Player("Mohamed Salah", Team("Liverpool", 2), 17)
            )
        )
    )
}
