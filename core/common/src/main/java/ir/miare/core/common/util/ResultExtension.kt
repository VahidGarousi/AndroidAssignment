package ir.miare.core.common.util


inline fun <T, E : AppError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Failure -> Result.Failure(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E : AppError> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

inline fun <T, E : AppError> Result<T, E>.onSuccess(
    action: (T) -> Unit,
): Result<T, E> = when (this) {
    is Result.Failure -> this
    is Result.Success -> {
        action(data)
        this
    }
}

inline fun <T, E : AppError> Result<T, E>.onError(
    action: (E) -> Unit,
): Result<T, E> {
    return when (this) {
        is Result.Failure -> {
            action(error)
            this
        }

        is Result.Success -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>
typealias SimpleResult<D> = Result<D, AppError>


fun <D, E : AppError> Result<D, E>.getOrNull(): D? = (this as? Result.Success)?.data

fun <D, E : AppError> Result<D, E>.getOrElse(
    default: () -> D,
): D = when (this) {
    is Result.Success -> data
    is Result.Failure -> default()
}

fun <D, E : AppError> Result<D, E>.exceptionOrNull(): E? =
    when (this) {
        is Result.Failure -> this.error
        else -> null
    }

inline fun <D, E : AppError> Result<D, E>.fold(
    onSuccess: (data: D) -> Unit = {},
    onFailure: (error: E) -> Unit = {},
) = when (this) {
    is Result.Success -> onSuccess(this.data)
    is Result.Failure -> onFailure(this.error)
}

