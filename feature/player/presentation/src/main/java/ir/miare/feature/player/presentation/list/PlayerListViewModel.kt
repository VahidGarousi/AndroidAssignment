package ir.miare.feature.player.presentation.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.miare.feature.player.domain.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val repository: PlayerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerListState())
    val uiState = _uiState.asStateFlow()

    init {
        getPlayers()
    }

    private fun getPlayers() {

    }
}