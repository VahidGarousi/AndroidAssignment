package ir.miare.core.ui.paginator.strategy

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult

interface PaginationStrategy<T> {
    suspend fun fetchNext(): Result<PaginatedResult<T>, DataError.Network>
    fun reset()
    fun hasMore(): Boolean
}