package ir.miare.core.ui.paginator.manager

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.fold
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.core.ui.paginator.strategy.PaginationStrategy
import kotlinx.collections.immutable.toPersistentList

class PaginationManager<T>(
    private val strategy: PaginationStrategy<T>,
    private val onStateUpdated: (PaginatedState<T>) -> Unit,
) {
    private var isLoading = false
    private val allItems = mutableListOf<T>()

    suspend fun loadNextPage() {
        if (isLoading || !strategy.hasMore()) return
        isLoading = true

        if (allItems.isEmpty()) {
            onStateUpdated(PaginatedState.InitialLoading)
        } else {
            onStateUpdated(PaginatedState.LoadingMore(allItems.toPersistentList()))
        }

        strategy.fetchNext().fold(
            onSuccess = { paginatedResult ->
                allItems.addAll(paginatedResult.data)
                onStateUpdated(
                    PaginatedState.Loaded(
                        data = allItems.toPersistentList(),
                        isEndReached = !strategy.hasMore(),
                    ),
                )
            },
            onFailure = { error: DataError.Network ->
                onStateUpdated(PaginatedState.Error(message = error))
            },
        )

        isLoading = false
    }

    suspend fun refresh() {
        strategy.reset()
        allItems.clear()
        loadNextPage()
    }
}
