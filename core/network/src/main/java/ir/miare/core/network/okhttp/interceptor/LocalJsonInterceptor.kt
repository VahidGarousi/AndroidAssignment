package ir.miare.core.network.okhttp.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal class LocalJsonInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Read JSON from assets
        val json = context.assets.open("data.json").bufferedReader().use { it.readText() }

        return Response.Builder()
            .code(200)
            .message(json)
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }
}