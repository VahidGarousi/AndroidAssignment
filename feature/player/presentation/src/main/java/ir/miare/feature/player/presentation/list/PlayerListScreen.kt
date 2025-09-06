package ir.miare.feature.player.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.miare.core.common.util.AppError
import ir.miare.core.designsystem.theme.MiarePreview
import ir.miare.core.designsystem.theme.MiareTheme
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.presentation.list.component.PlayerCard


@Composable
fun PlayerListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onAction(PlayerListAction.LoadNextPage)
    }
    PlayerListScreen(
        modifier = modifier,
        state = state,
        onAction = viewModel::onAction
    )
}


@Composable
private fun PlayerListScreen(
    modifier: Modifier = Modifier,
    state: PlayerListState,
    onAction: (PlayerListAction) -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MiareTheme.colors.bg.body
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            when (state.paginatedLeagues) {
                is PaginatedState.Error -> ErrorContent(
                    modifier = modifier,
                    appError = state.paginatedLeagues.message
                )

                PaginatedState.InitialLoading -> {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PaginatedState.Loaded -> {
                    if (state.paginatedLeagues.data.isEmpty()) {
                        Box(
                            modifier = modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Nothing to show")
                        }
                    } else {
                        LoadedPlayers(
                            paginatedState = state.paginatedLeagues,
                            isEndReached = state.paginatedLeagues.isEndReached,
                            onAction = onAction,
                            modifier = modifier
                        )
                    }

                }

                is PaginatedState.LoadingMore -> {
                    val players = remember(state.paginatedLeagues) {
                        state.paginatedLeagues.data.flatMap { leagueList ->
                            leagueList.teams.flatMap { team ->
                                team.players
                            }
                        }
                    }
                    LazyColumn(
                        modifier = modifier.fillMaxWidth(),
                        state = rememberLazyListState(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = players
                        ) { player ->
                            PlayerCard(
                                player = player,
                                containerColor = MiareTheme.colors.bg.card
                            )
                        }
                    }
                    Box(
                        modifier = modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                PaginatedState.NotLoaded -> {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadedPlayers(
    paginatedState : PaginatedState<LeagueList>,
    isEndReached : Boolean,
    onAction: (PlayerListAction) -> Unit,
    modifier: Modifier
) {
    val lazyListState = rememberLazyListState()
    val players = remember(paginatedState) {
        paginatedState.data?.flatMap { leagueList ->
            leagueList.teams.flatMap { team ->
                team.players
            }
        } ?: emptyList()
    }
    val shouldLoadMore by remember(
        paginatedState,
        isEndReached
    ) {
        derivedStateOf {
            if (isEndReached) return@derivedStateOf false
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false
            val lastVisibleItemIndex =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisibleItemIndex >= totalItems - 1 - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onAction(PlayerListAction.LoadNextPage)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = players
        ) { player ->
            PlayerCard(
                player = player,
                containerColor = MiareTheme.colors.bg.card
            )
        }
    }
}

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    appError: AppError
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "" + appError
        )
    }
}

private const val LOAD_MORE_THRESHOLD = 4


@PreviewLightDark
@Composable
private fun PlayerListScreenPreview(
    @PreviewParameter(PlayerListStatePreviewProvider::class)
    state: PlayerListState
) {
    MiarePreview {
        PlayerListScreen(
            state = state
        )
    }
}