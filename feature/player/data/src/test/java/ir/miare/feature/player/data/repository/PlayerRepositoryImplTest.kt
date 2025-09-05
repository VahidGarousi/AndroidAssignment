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
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team
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
    fun `given API returns leagues, when getLeagues called, then returns paginated leagues`() = runTest {
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

        val result = repository.getLeagues(page = 1, limit = 1, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Success(
            PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = League(name = "La Liga", country = "Spain", rank = 1, totalMatches = 38),
                        players = listOf(Player(name = "Player1", team = Team("Team1", 1), totalGoal = 10))
                    )
                ),
                totalPages = 2
            )
        )
        coVerify(exactly = 1) { apiService.getLeagueList() }
    }

    @Test
    fun `given API returns empty list, when getLeagues called, then returns empty paginated result`() = runTest {
        coEvery { apiService.getLeagueList() } returns emptyList()

        val result = repository.getLeagues(page = 1, limit = 10, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Success(PaginatedResult(emptyList(), totalPages = 0))
        coVerify(exactly = 1) { apiService.getLeagueList() }
    }

    @Test
    fun `given API throws generic exception, when getLeagues called, then returns network failure`() = runTest {
        coEvery { apiService.getLeagueList() } throws RuntimeException("API failure")

        val result = repository.getLeagues(page = 1, limit = 10, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Failure(DataError.Network.UNKNOWN)
    }

    @Test
    fun `given multiple leagues, when limit is 2 and sorted by rank, then returns first two leagues`() = runTest {
        val apiResponse = listOf(
            LeagueListItemDto(LeagueDto("CountryA", "LeagueB", 2, 38), emptyList()),
            LeagueListItemDto(LeagueDto("CountryB", "LeagueA", 1, 38), emptyList()),
            LeagueListItemDto(LeagueDto("CountryC", "LeagueC", 3, 38), emptyList())
        )
        coEvery { apiService.getLeagueList() } returns apiResponse

        val result = repository.getLeagues(
            page = 1,
            limit = 2,
            sortingStrategy = LeagueListSortingStrategy.ByLeagueRanking(LeagueListSortingStrategy.Direction.ASCENDING)
        )

        result shouldBe Result.Success(
            PaginatedResult(
                data = listOf(
                    LeagueList(League(name = "LeagueA", country = "CountryB", rank = 1, totalMatches = 38), emptyList()),
                    LeagueList(League(name = "LeagueB", country = "CountryA", rank = 2, totalMatches = 38), emptyList())
                ),
                totalPages = 2
            )
        )
        coVerify(exactly = 1) { apiService.getLeagueList() }
    }

    @Test
    fun `given requested page exceeds total pages, when getLeagues called, then returns empty list`() = runTest {
        coEvery { apiService.getLeagueList() } returns listOf(
            LeagueListItemDto(LeagueDto("Country", "LeagueA", 1, 38), emptyList())
        )

        val result = repository.getLeagues(page = 2, limit = 1, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Success(PaginatedResult(data = emptyList(), totalPages = 1))
    }

    @Test
    fun `given API throws IOException, when getLeagues called, then returns NO_INTERNET`() = runTest {
        coEvery { apiService.getLeagueList() } throws IOException()

        val result = repository.getLeagues(page = 1, limit = 10, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Failure(DataError.Network.NO_INTERNET)
    }

    @Test
    fun `given API throws HttpException 404, when getLeagues called, then returns NOT_FOUND`() = runTest {
        coEvery { apiService.getLeagueList() } throws HttpException(Response.error<Any>(404, mockk(relaxed = true)))

        val result = repository.getLeagues(page = 1, limit = 10, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Failure(DataError.Network.NOT_FOUND)
    }

    @Test
    fun `given API throws SerializationException, when getLeagues called, then returns SERIALIZATION error`() = runTest {
        coEvery { apiService.getLeagueList() } throws SerializationException("bad json")

        val result = repository.getLeagues(page = 1, limit = 10, sortingStrategy = LeagueListSortingStrategy.None)

        result shouldBe Result.Failure(DataError.Network.SERIALIZATION)
    }

    @Test
    fun `given multiple leagues with players, when sorted ascending by rank, then returns sorted leagues with full player data`() = runTest {
        val apiResponse = listOf(
            LeagueListItemDto(
                LeagueDto("CountryZ", "Z League", 2, 38),
                listOf(PlayerDto("PlayerZ1", TeamDto("TeamZ1", 1), 10))
            ),
            LeagueListItemDto(
                LeagueDto("CountryA", "A League", 1, 38),
                listOf(PlayerDto("PlayerA1", TeamDto("TeamA1", 2), 12))
            ),
            LeagueListItemDto(
                LeagueDto("CountryM", "M League", 3, 38),
                listOf(PlayerDto("PlayerM1", TeamDto("TeamM1", 3), 8))
            )
        )
        coEvery { apiService.getLeagueList() } returns apiResponse

        val result = repository.getLeagues(
            page = 1,
            limit = 10,
            sortingStrategy = LeagueListSortingStrategy.ByLeagueRanking(LeagueListSortingStrategy.Direction.ASCENDING)
        )

        val leagues = (result as Result.Success).data.data

        // Verify league order
        leagues.map { it.league.name } shouldBe listOf("A League", "Z League", "M League")

        // Verify first league's player
        val firstLeaguePlayers = leagues[0].players
        firstLeaguePlayers.size shouldBe 1
        firstLeaguePlayers[0].name shouldBe "PlayerA1"
        firstLeaguePlayers[0].totalGoal shouldBe 12
        firstLeaguePlayers[0].team.name shouldBe "TeamA1"
        firstLeaguePlayers[0].team.rank shouldBe 2

        // Verify second league's player
        val secondLeaguePlayers = leagues[1].players
        secondLeaguePlayers.size shouldBe 1
        secondLeaguePlayers[0].name shouldBe "PlayerZ1"
        secondLeaguePlayers[0].totalGoal shouldBe 10
        secondLeaguePlayers[0].team.name shouldBe "TeamZ1"
        secondLeaguePlayers[0].team.rank shouldBe 1

        // Verify third league's player
        val thirdLeaguePlayers = leagues[2].players
        thirdLeaguePlayers.size shouldBe 1
        thirdLeaguePlayers[0].name shouldBe "PlayerM1"
        thirdLeaguePlayers[0].totalGoal shouldBe 8
        thirdLeaguePlayers[0].team.name shouldBe "TeamM1"
        thirdLeaguePlayers[0].team.rank shouldBe 3

        coVerify(exactly = 1) { apiService.getLeagueList() }
    }

}
