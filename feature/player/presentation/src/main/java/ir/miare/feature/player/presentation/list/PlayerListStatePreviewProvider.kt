package ir.miare.feature.player.presentation.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ir.miare.core.common.util.DataError
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team
import kotlinx.collections.immutable.persistentListOf

internal class PlayerListStatePreviewProvider : PreviewParameterProvider<PlayerListState> {

    override val values = sequenceOf(
        notLoadedState(),
        initialLoadingState(),
        loadedState(),
        loadingMoreState(),
        errorState()
    )

    private fun notLoadedState() = PlayerListState(
        paginatedLeagues = PaginatedState.NotLoaded,
        sortingStrategy = LeagueListSortingStrategy.None
    )

    private fun initialLoadingState() = PlayerListState(
        paginatedLeagues = PaginatedState.InitialLoading,
        sortingStrategy = LeagueListSortingStrategy.None
    )

    private fun loadedState() = PlayerListState(
        paginatedLeagues = PaginatedState.Loaded(
            data = persistentListOf(sampleLeagueList()),
            isEndReached = true
        ),
        sortingStrategy = LeagueListSortingStrategy.None
    )

    private fun loadingMoreState() = PlayerListState(
        paginatedLeagues = PaginatedState.LoadingMore(
            data = persistentListOf(sampleLeagueList())
        ),
        sortingStrategy = LeagueListSortingStrategy.None
    )

    private fun errorState() = PlayerListState(
        paginatedLeagues = PaginatedState.Error(message = DataError.Network.NO_INTERNET),
        sortingStrategy = LeagueListSortingStrategy.None
    )

    // Helper to create a sample LeagueList
    private fun sampleLeagueList() = LeagueList(
        league = sampleLeague(country = "Spain", name = "La Liga", rank = 1, totalMatches = 38),
        teams = listOf(
            sampleTeam(
                name = "Real Madrid",
                rank = 1,
                players = listOf(
                    samplePlayer("Jude Bellingham", 19),
                    samplePlayer("Vinicius Junior", 15)
                )
            ),
            sampleTeam(
                name = "Barcelona",
                rank = 2,
                players = listOf(
                    samplePlayer("Robert Lewandowski", 15),
                    samplePlayer("Ansu Fati", 10)
                )
            )
        )
    )

    private fun sampleLeague(country: String, name: String, rank: Int, totalMatches: Int) = League(
        country = country,
        name = name,
        rank = rank,
        totalMatches = totalMatches
    )

    private fun sampleTeam(name: String, rank: Int, players: List<Player>) = Team(
        name = name,
        rank = rank,
        players = players
    )

    private fun samplePlayer(name: String, totalGoal: Int) = Player(
        name = name,
        totalGoal = totalGoal
    )
}
