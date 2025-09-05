package ir.miare.feature.player.data.repository


import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.data.model.LeagueDto
import ir.miare.feature.player.data.model.LeagueListItemDto
import ir.miare.feature.player.data.model.PlayerDto
import ir.miare.feature.player.data.model.TeamDto
import ir.miare.feature.player.data.remote.api.retrofit.PlayerApiService
import ir.miare.feature.player.domain.model.League
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class PlayerRepositoryImplTest {

    private lateinit var apiService: PlayerApiService
    private lateinit var repository: PlayerRepositoryImpl

    @BeforeEach
    fun setup() {
        apiService = mockk()
        repository = PlayerRepositoryImpl(apiService)
    }

    @Test
    fun `given API returns leagues, when getLeagues called, then return paginated result`() =
        runTest {
            // Arrange
            val apiResponse = listOf(
                LeagueListItemDto(
                    league = LeagueDto("Spain", "La Liga", 1, 38),
                    players = listOf(PlayerDto("Player1", TeamDto("Team1", 1), 10))
                ),
                LeagueListItemDto(
                    league = LeagueDto("England", "Premier League", 2, 38),
                    players = listOf(PlayerDto("Player2", TeamDto("Team2", 2), 15))
                )
            )
            coEvery { apiService.getLeagueList() } returns apiResponse

            // Act
            val result = repository.getLeagues(page = 1, limit = 1)

            // Assert
            result shouldBe Result.Success(
                PaginatedResult(
                    data = listOf(
                        League(name = "La Liga", country = "Spain", rank = 1, totalMatches = 38)
                    ),
                    totalPages = 2
                )
            )
            coVerify(exactly = 1) { apiService.getLeagueList() }
        }

    @Test
    fun `given API returns empty list, when getLeagues called, then return empty paginated result`() =
        runTest {
            // Arrange
            coEvery { apiService.getLeagueList() } returns emptyList()

            // Act
            val result = repository.getLeagues(page = 1, limit = 10)

            // Assert
            result shouldBe Result.Success(PaginatedResult(emptyList(), totalPages = 0))
            coVerify(exactly = 1) { apiService.getLeagueList() }
        }

    @Test
    fun `given API throws exception, when getLeagues called, then returns network failure`() = runTest {
        // Arrange
        val exception = RuntimeException("API failure")
        coEvery { apiService.getLeagueList() } throws exception

        // Act
        val result = repository.getLeagues(page = 1, limit = 10)

        // Assert
        result shouldBe Result.Failure(DataError.Network.UNKNOWN)
        coVerify(exactly = 1) { apiService.getLeagueList() }
    }

    @Test
    fun `given multiple leagues, when limit is 2, then return first two sorted by rank`() =
        runTest {
            // Arrange
            val apiResponse = listOf(
                LeagueListItemDto(LeagueDto("CountryA", "LeagueB", 2, 38), emptyList()),
                LeagueListItemDto(LeagueDto("CountryB", "LeagueA", 1, 38), emptyList()),
                LeagueListItemDto(LeagueDto("CountryC", "LeagueC", 3, 38), emptyList())
            )
            coEvery { apiService.getLeagueList() } returns apiResponse

            // Act
            val result = repository.getLeagues(page = 1, limit = 2)

            // Assert
            result shouldBe Result.Success(
                PaginatedResult(
                    data = listOf(
                        League(name = "LeagueA", country = "CountryB", rank = 1, totalMatches = 38),
                        League(name = "LeagueB", country = "CountryA", rank = 2, totalMatches = 38)
                    ),
                    totalPages = 2
                )
            )
        }

    @Test
    fun `given page out of bounds, when getLeagues called, then return empty list`() = runTest {
        // Arrange
        val apiResponse = listOf(
            LeagueListItemDto(LeagueDto("Country", "LeagueA", 1, 38), emptyList())
        )
        coEvery { apiService.getLeagueList() } returns apiResponse

        // Act
        val result = repository.getLeagues(page = 2, limit = 1)

        // Assert
        result shouldBe Result.Success(
            PaginatedResult(
                data = emptyList(),
                totalPages = 1
            )
        )
    }


    @Test
    fun `given API throws IOException, when getLeagues called, then returns NO_INTERNET`() = runTest {
        coEvery { apiService.getLeagueList() } throws IOException()
        val result = repository.getLeagues(page = 1, limit = 10)
        result shouldBe Result.Failure(DataError.Network.NO_INTERNET)
    }

    @Test
    fun `given API throws HttpException 404, when getLeagues called, then returns NOT_FOUND`() = runTest {
        coEvery { apiService.getLeagueList() } throws HttpException(
            Response.error<Any>(
                404,
                mockk(relaxed = true)
            )
        )
        val result = repository.getLeagues(page = 1, limit = 10)
        result shouldBe Result.Failure(DataError.Network.NOT_FOUND)
    }

    @Test
    fun `given API throws SerializationException, when getLeagues called, then returns SERIALIZATION`() = runTest {
        coEvery { apiService.getLeagueList() } throws SerializationException("bad json")
        val result = repository.getLeagues(page = 1, limit = 10)
        result shouldBe Result.Failure(DataError.Network.SERIALIZATION)
    }
}
