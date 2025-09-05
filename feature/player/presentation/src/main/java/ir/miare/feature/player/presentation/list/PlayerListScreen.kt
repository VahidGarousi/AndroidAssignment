package ir.miare.feature.player.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.miare.core.common.util.AppError
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.presentation.list.component.LeagueCard

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
                    val lazyListState = rememberLazyListState()
                    val shouldLoadMore by remember(
                        state.paginatedLeagues.data,
                        state.paginatedLeagues.isEndReached
                    ) {
                        derivedStateOf {
                            if (state.paginatedLeagues.isEndReached) return@derivedStateOf false
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
                        modifier = modifier.fillMaxSize(),
                        state = lazyListState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = state.paginatedLeagues.data
                        ) { leagueList ->
                            LeagueCard(list = leagueList)
                        }
                    }
                }

            }

            is PaginatedState.LoadingMore<*> -> {
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
