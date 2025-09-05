package ir.miare.core.ui.paginator.manager

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.common.util.onSuccess
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.core.ui.paginator.strategy.PaginationStrategy


class PageBasedPaginationManager<T>(
    private val initialPage: Int = 1,
    private val fetch: suspend (page: Int) -> Result<PaginatedResult<T>, DataError.Network>,
) : PaginationStrategy<T> {
    private var currentPage = initialPage
    private var isLastPage = false

    override suspend fun fetchNext(): Result<PaginatedResult<T>, DataError.Network> {
        val result = fetch(currentPage)
        result.onSuccess {
            isLastPage = currentPage >= it.totalPages
            currentPage++
        }
        return result
    }

    override fun reset() {
        currentPage = initialPage
        isLastPage = false
    }

    override fun hasMore() = !isLastPage
}
