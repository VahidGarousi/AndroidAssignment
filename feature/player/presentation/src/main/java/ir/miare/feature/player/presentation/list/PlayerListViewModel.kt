package ir.miare.feature.player.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.miare.core.ui.paginator.manager.PageBasedPaginationManager
import ir.miare.core.ui.paginator.manager.PaginationManager
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.usecase.GetLeagueListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val getLeagueListUseCase: GetLeagueListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerListState())
    val uiState = _uiState.asStateFlow()

    private val playersPaginationManager by lazy {
        PaginationManager(
            strategy = PageBasedPaginationManager(
                initialPage = 1,
                fetch = { page ->
                    getLeagueListUseCase(
                        page = page,
                        limit = 5,
                        sortingStrategy = _uiState.value.sortingStrategy
                    )
                }
            )
        ) { newState ->
            _uiState.update { it.copy(paginatedLeagues = newState) }
        }
    }

    fun onAction(action: PlayerListAction) {
        when (action) {
            is PlayerListAction.LoadNextPage -> loadLeaguesNextPage()
            is PlayerListAction.ChangeSortingStrategy -> changeSortingStrategy(action.sortingStrategy)
        }
    }

    private fun changeSortingStrategy(
        strategy: LeagueListSortingStrategy
    ) {
        _uiState.update {
            it.copy(
                sortingStrategy = strategy,
                paginatedLeagues = PaginatedState.NotLoaded
            )
        }
        viewModelScope.launch {
            playersPaginationManager.refresh()
        }
    }

    private fun loadLeaguesNextPage() {
        viewModelScope.launch {
            playersPaginationManager.loadNextPage()
        }
    }
}