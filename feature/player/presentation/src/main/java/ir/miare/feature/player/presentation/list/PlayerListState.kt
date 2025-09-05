package ir.miare.feature.player.presentation.list

import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy

data class PlayerListState(
    val paginatedLeagues: PaginatedState<LeagueList> = PaginatedState.NotLoaded,
    val sortingStrategy: LeagueListSortingStrategy = LeagueListSortingStrategy.None
)
