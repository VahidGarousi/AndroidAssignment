package ir.miare.feature.player.data

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okio.buffer
import okio.source

fun MockWebServer.enqueueResponse(fileName: String, code: Int) {
    val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")

    val source = inputStream?.let { inputStream.source().buffer() }
    source?.let {
        enqueue(
            MockResponse.Builder()
                .code(code)
                .body(source.readString(java.nio.charset.StandardCharsets.UTF_8))
                .build()
        )
    }
}