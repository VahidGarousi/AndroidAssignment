package ir.miare.feature.player.presentation.list

import ir.miare.feature.player.domain.model.LeagueListSortingStrategy

sealed interface PlayerListAction {
    data object LoadNextPage : PlayerListAction
    data class ChangeSortingStrategy(
        val sortingStrategy: LeagueListSortingStrategy
    ) : PlayerListAction
}