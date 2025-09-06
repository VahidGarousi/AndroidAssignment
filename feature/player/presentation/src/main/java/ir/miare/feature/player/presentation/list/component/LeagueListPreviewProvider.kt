package ir.miare.feature.player.presentation.list.component

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team

class LeagueListPreviewProvider : PreviewParameterProvider<LeagueList> {
    override val values = sequenceOf(
        LeagueList(
            league = League(
                country = "Spain",
                name = "La Liga",
                rank = 1,
                totalMatches = 38
            ),
            teams = listOf(
                Team(
                    name = "Real Madrid",
                    rank = 1,
                    players = listOf(
                        Player(name = "Jude Bellingham", totalGoal = 19),
                        Player(name = "Vinicius Junior", totalGoal = 15)
                    )
                )
            )
        ),
        // Uncomment and adjust additional leagues if needed
//        LeagueList(
//            league = League(
//                country = "England",
//                name = "Premier League",
//                rank = 2,
//                totalMatches = 38
//            ),
//            teams = listOf(
//                Team(
//                    name = "Tottenham",
//                    rank = 3,
//                    players = listOf(Player(name = "Harry Kane", totalGoal = 18))
//                ),
//                Team(
//                    name = "Liverpool",
//                    rank = 2,
//                    players = listOf(Player(name = "Mohamed Salah", totalGoal = 17))
//                )
//            )
//        )
    )
}
