package ir.miare.feature.player.presentation.list

import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.League

data class PlayerListState(
    val paginatedLeagues: PaginatedState<League> = PaginatedState.NotLoaded,
)
