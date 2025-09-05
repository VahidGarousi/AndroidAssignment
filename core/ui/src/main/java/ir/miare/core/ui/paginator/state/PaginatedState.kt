package ir.miare.core.ui.paginator.state

import ir.miare.core.common.util.AppError
import kotlinx.collections.immutable.ImmutableList

sealed class PaginatedState<out T> {
    abstract val data: ImmutableList<T>?

    data object NotLoaded : PaginatedState<Nothing>() {
        override val data: Nothing? = null
    }

    data object InitialLoading : PaginatedState<Nothing>() {
        override val data: Nothing? = null
    }

    data class LoadingMore<T>(
        override val data: ImmutableList<T>,
    ) : PaginatedState<T>()

    data class Loaded<T>(
        override val data: ImmutableList<T>,
        val isEndReached: Boolean,
    ) : PaginatedState<T>()

    data class Error(
        val message: AppError,
    ) : PaginatedState<Nothing>() {
        override val data: ImmutableList<Nothing>? = null
    }
}

fun <T> PaginatedState<T>.hasMore(): Boolean =
    when (this) {
        is PaginatedState.Loaded -> !isEndReached
        is PaginatedState.NotLoaded,
        is PaginatedState.InitialLoading,
        is PaginatedState.LoadingMore,
        is PaginatedState.Error,
        -> true
    }
