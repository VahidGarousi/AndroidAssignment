package ir.miare.core.network.okhttp.interceptor

import ir.miare.core.network.okhttp.exception.NoInternetException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AppErrorInterceptor @Inject constructor() : Interceptor {
    override fun intercept(
        chain: Interceptor.Chain,
    ): Response {
        val request = chain.request()
        val response = try {
            chain.proceed(request)
        } catch (exception: IOException) {
            throw NoInternetException()
        }
        if (!response.isSuccessful) {
            throw ApiErrorException()
        }
        return response
    }
}