package ir.miare.core.domain.model

data class PaginatedResult<T>(
    val data: List<T>,
    val totalPages: Int,
)
