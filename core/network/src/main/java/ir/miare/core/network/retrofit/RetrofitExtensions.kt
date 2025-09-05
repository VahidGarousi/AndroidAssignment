package ir.miare.core.network.retrofit


import ir.miare.core.common.util.AppError
import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T : Any, E : AppError> safeCall(
    @Suppress("UNCHECKED_CAST")
    mapError: (DataError.Network) -> E = { it as E },
    block: suspend () -> T,
): Result<T, E> {
    var finalResult: Result<T, E>
    try {
        val result = block()
        finalResult = if (result is Response<*>) {
            @Suppress("UNCHECKED_CAST")
            val response = result as Response<T>
            when {
                !response.isSuccessful -> Result.Failure(mapError(mapHttpError(response.code())))
                response.body() == null -> Result.Failure(mapError(DataError.Network.SERVER_ERROR))
                else -> Result.Success(response.body()!!)
            }
        } else {
            Result.Success(result)
        }
    } catch (exception: IOException) {
        finalResult = Result.Failure(mapError(DataError.Network.NO_INTERNET))
    } catch (exception: HttpException) {
        finalResult = Result.Failure(mapError(mapHttpError(exception.code())))
    } catch (exception: Exception) {
        finalResult = Result.Failure(mapError(mapExceptionToDataError(exception)))
    }
    return finalResult
}

fun mapHttpError(
    code: Int,
): DataError.Network =
    when (code) {
        HttpCode.HTTP_UNAUTHORIZED -> DataError.Network.UNAUTHORIZED
        HttpCode.HTTP_NOT_FOUND -> DataError.Network.NOT_FOUND
        HttpCode.HTTP_CONFLICT -> DataError.Network.CONFLICT
        HttpCode.HTTP_PAYLOAD_TOO_LARGE -> DataError.Network.PAYLOAD_TOO_LARGE
        HttpCode.HTTP_TOO_MANY_REQUESTS -> DataError.Network.TOO_MANY_REQUESTS
        in HttpCode.HTTP_SERVER_ERROR_START..HttpCode.HTTP_SERVER_ERROR_END -> DataError.Network.SERVER_ERROR
        else -> DataError.Network.UNKNOWN
    }

private object HttpCode {
    const val HTTP_UNAUTHORIZED = 401
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_PAYLOAD_TOO_LARGE = 413
    const val HTTP_TOO_MANY_REQUESTS = 429
    const val HTTP_SERVER_ERROR_START = 500
    const val HTTP_SERVER_ERROR_END = 599
}

fun mapExceptionToDataError(
    e: Exception,
): DataError.Network =
    when (e) {
        is java.net.SocketTimeoutException -> DataError.Network.REQUEST_TIMEOUT
        is java.net.UnknownHostException -> DataError.Network.NO_INTERNET
        is HttpException -> mapHttpError(e.code())
        is SerializationException -> DataError.Network.SERIALIZATION
        else -> DataError.Network.UNKNOWN
    }