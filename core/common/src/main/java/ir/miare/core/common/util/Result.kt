package ir.miare.core.common.util

sealed interface Result<out D, out E : AppError> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Failure<out E : AppError>(val error: E) : Result<Nothing, E>
}
