package ir.miare.feature.player.data.repository

import io.kotest.matchers.shouldBe
import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.feature.player.data.enqueueResponse
import ir.miare.feature.player.data.remote.api.retrofit.PlayerApiService
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
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
        mockWebServer = MockWebServer().apply { start() }

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create()
        repository = PlayerRepositoryImpl(apiService)
    }

    @AfterEach
    fun teardown() {
        mockWebServer.close()
    }

    @Test
    fun `given JSON response, when getLeagues called, then returns parsed leagues`() = runTest {
        mockWebServer.enqueueResponse("leagues-200.json", 200)

        val result = repository.getLeagues(
            page = 1,
            limit = 5,
            sortingStrategy = LeagueListSortingStrategy.None
        )

        result shouldBe Result.Success(
            (result as Result.Success).data.apply {
                data.size shouldBe 3
                data.first().league.name shouldBe "La Liga"
            }
        )
    }

    @Test
    fun `given empty JSON response, when getLeagues called, then returns empty list`() = runTest {
        mockWebServer.enqueue(MockResponse.Builder().code(200).body("[]").build())

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = ir.miare.feature.player.domain.model.LeagueListSortingStrategy.None
        )

        (result as Result.Success).data.data.size shouldBe 0
    }

    @Test
    fun `given 404 response, when getLeagues called, then returns NOT_FOUND`() = runTest {
        mockWebServer.enqueue(MockResponse.Builder().code(404).build())

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = ir.miare.feature.player.domain.model.LeagueListSortingStrategy.None
        )

        (result as Result.Failure).error shouldBe DataError.Network.NOT_FOUND
    }

    @Test
    fun `given 500 response, when getLeagues called, then returns SERVER_ERROR`() = runTest {
        mockWebServer.enqueue(MockResponse.Builder().code(500).build())

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = ir.miare.feature.player.domain.model.LeagueListSortingStrategy.None
        )

        (result as Result.Failure).error shouldBe DataError.Network.SERVER_ERROR
    }

    @Test
    fun `given malformed JSON, when getLeagues called, then returns SERIALIZATION failure`() =
        runTest {
            mockWebServer.enqueue(MockResponse.Builder().code(200).body("{ invalid json }").build())

            val result = repository.getLeagues(
                page = 1,
                limit = 5,
                sortingStrategy = ir.miare.feature.player.domain.model.LeagueListSortingStrategy.None
            )

            (result as Result.Failure).error shouldBe DataError.Network.SERIALIZATION
        }

    @Test
    fun `given page exceeds total pages, when getLeagues called, then returns empty list`() =
        runTest {
            mockWebServer.enqueueResponse("leagues-200.json", 200)

            val result = repository.getLeagues(
                page = 100,
                limit = 5,
                sortingStrategy = ir.miare.feature.player.domain.model.LeagueListSortingStrategy.None
            )

            (result as Result.Success).data.data.size shouldBe 0
        }

    @Test
    fun `given leagues JSON, when sorted ascending by rank, then returns sorted leagues with players`() = runTest {
        mockWebServer.enqueueResponse("leagues-200.json", 200)

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = LeagueListSortingStrategy.ByLeagueRanking(
                LeagueListSortingStrategy.Direction.ASCENDING
            )
        )

        val leagues = (result as Result.Success).data.data
        // Verify league order
        leagues.map { it.league.name } shouldBe listOf(
            "Premier League", "La Liga", "Bundesliga"
        )

        // Verify first league's players
        val premierLeaguePlayers = leagues[0].players
        premierLeaguePlayers.size shouldBe 5
        premierLeaguePlayers[0].name shouldBe "Victor Osimhen"
        premierLeaguePlayers[0].totalGoal shouldBe 22
        premierLeaguePlayers[0].team.name shouldBe "Napoli"
        premierLeaguePlayers[0].team.rank shouldBe 1

        // Verify second league's players
        val laLigaPlayers = leagues[1].players
        laLigaPlayers.size shouldBe 5
        laLigaPlayers[0].name shouldBe "Arnaud Kalimuendo"
        laLigaPlayers[0].totalGoal shouldBe 17
        laLigaPlayers[0].team.name shouldBe "Rennes"
        laLigaPlayers[0].team.rank shouldBe 3

        // Verify third league has no players
        leagues[2].players shouldBe emptyList()
    }

    @Test
    fun `given leagues JSON, when sorted descending by rank, then returns sorted leagues with players`() = runTest {
        mockWebServer.enqueueResponse("leagues-200.json", 200)

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = LeagueListSortingStrategy.ByLeagueRanking(
                LeagueListSortingStrategy.Direction.DESCENDING
            )
        )

        val leagues = (result as Result.Success).data.data
        // Verify league order
        leagues.map { it.league.name } shouldBe listOf(
            "Bundesliga", "La Liga", "Premier League"
        )

        // Verify second league's players (La Liga)
        val laLigaPlayers = leagues[1].players
        laLigaPlayers.size shouldBe 5
        laLigaPlayers[0].name shouldBe "Arnaud Kalimuendo"
        laLigaPlayers[0].totalGoal shouldBe 17
        laLigaPlayers[0].team.name shouldBe "Rennes"
        laLigaPlayers[0].team.rank shouldBe 3

        // Verify third league's players (Premier League)
        val premierLeaguePlayers = leagues[2].players
        premierLeaguePlayers.size shouldBe 5
        premierLeaguePlayers[0].name shouldBe "Victor Osimhen"
        premierLeaguePlayers[0].totalGoal shouldBe 22
        premierLeaguePlayers[0].team.name shouldBe "Napoli"
        premierLeaguePlayers[0].team.rank shouldBe 1

        // Verify first league has no players
        leagues[0].players shouldBe emptyList()
    }

    @Test
    fun `given leagues JSON, when sorting strategy is None, then maintains API order with full data`() = runTest {
        mockWebServer.enqueueResponse("leagues-200.json", 200)

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = LeagueListSortingStrategy.None
        )

        val leagues = (result as Result.Success).data.data
        leagues.map { it.league.name } shouldBe listOf(
            "La Liga", "Premier League", "Bundesliga"
        )

        // Check first league's first player
        val laLigaPlayers = leagues[0].players
        laLigaPlayers.size shouldBe 5
        laLigaPlayers[0].name shouldBe "Arnaud Kalimuendo"
        laLigaPlayers[0].totalGoal shouldBe 17
        laLigaPlayers[0].team.name shouldBe "Rennes"
        laLigaPlayers[0].team.rank shouldBe 3
    }

}
