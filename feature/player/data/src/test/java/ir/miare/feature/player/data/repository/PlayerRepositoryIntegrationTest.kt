package ir.miare.feature.player.data.repository

import ir.miare.core.common.util.Result
import ir.miare.feature.player.data.enqueueResponse
import ir.miare.feature.player.data.remote.api.retrofit.PlayerApiService
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

internal class PlayerRepositoryIntegrationTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: PlayerApiService
    private lateinit var repository: PlayerRepositoryImpl
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // point to MockWebServer
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create<PlayerApiService>()
        repository = PlayerRepositoryImpl(apiService)
    }

    @AfterEach
    fun teardown() {
        mockWebServer.close()
    }

    @Test
    fun `given JSON response, when getLeagues called, then return parsed leagues paginated`() =
        runTest {
            // Arrange: enqueue the JSON response
            mockWebServer.enqueueResponse("leagues-200.json", 200)
            // Act
            val result = repository.getLeagues(page = 1, limit = 5)

            // Assert
            assert(result is Result.Success)
            val successResult = result as Result.Success
            assertEquals(3, successResult.data.data.size) // first page with 5 leagues
            assertEquals(
                "Premier League",
                successResult.data.data.first().name
            ) // sorted by rank ascending
        }

    @Test
    fun `given empty JSON response, when getLeagues called, then return empty list`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body("[]")
                .build()
        )

        // Act
        val result = repository.getLeagues(page = 1, limit = 10)

        // Assert
        assert(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(0, successResult.data.data.size)
    }

    @Test
    fun `given 404 response, when getLeagues called, then return failure NOT_FOUND`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(404)
                .build()
        )

        // Act
        val result = repository.getLeagues(page = 1, limit = 10)

        // Assert
        assert(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals(ir.miare.core.common.util.DataError.Network.NOT_FOUND, failureResult.error)
    }

    @Test
    fun `given server error 500, when getLeagues called, then return failure SERVER_ERROR`() =
        runTest {
            // Arrange
            mockWebServer.enqueue(MockResponse.Builder().code(500).build())

            // Act
            val result = repository.getLeagues(page = 1, limit = 10)

            // Assert
            assert(result is Result.Failure)
            val failureResult = result as Result.Failure
            assertEquals(
                ir.miare.core.common.util.DataError.Network.SERVER_ERROR,
                failureResult.error
            )
        }

    @Test
    fun `given malformed JSON, when getLeagues called, then return SERIALIZATION failure`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse.Builder().code(200).body("{ invalid json }").build()
            )

            val result = repository.getLeagues(page = 1, limit = 5)

            assert(result is Result.Failure)
            val failure = result as Result.Failure
            assertEquals(ir.miare.core.common.util.DataError.Network.SERIALIZATION, failure.error)
        }

    @Test
    fun `given page exceeds total pages, then return empty list`() = runTest {
        mockWebServer.enqueueResponse("leagues-200.json", 200)

        val result = repository.getLeagues(page = 100, limit = 5)

        assert(result is Result.Success)
        val success = result as Result.Success
        assertEquals(0, success.data.data.size)
    }


}