package ir.miare.feature.player.domain.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team
import ir.miare.feature.player.domain.repository.PlayerRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetLeagueListUseCaseTest {

    private lateinit var repository: PlayerRepository
    private lateinit var getLeagueListUseCase: GetLeagueListUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        getLeagueListUseCase = GetLeagueListUseCase(repository)
    }

    @Test
    fun `given repository returns success, when use case is invoked, then result is success`() =
        runTest {
            val expected = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = League("La Liga", "Spain", 1, 38),
                        teams = emptyList()
                    )
                ),
                totalPages = 1
            )

            coEvery { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) } returns Result.Success(expected)

            val result = getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)

            result shouldBe Result.Success(expected)
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository returns network error, when use case is invoked, then result is failure`() =
        runTest {
            val error = DataError.Network.NO_INTERNET
            coEvery { repository.getLeagues(2, 20, LeagueListSortingStrategy.None) } returns Result.Failure(error)

            val result = getLeagueListUseCase(2, 20, LeagueListSortingStrategy.None)

            result shouldBe Result.Failure(error)
            coVerify(exactly = 1) { repository.getLeagues(2, 20, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository returns empty list, when use case is invoked, then result is success with empty data`() =
        runTest {
            val emptyResult = PaginatedResult<LeagueList>(
                data = emptyList(),
                totalPages = 1
            )

            coEvery { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) } returns Result.Success(emptyResult)

            val result = getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)

            result shouldBe Result.Success(emptyResult)
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository called with multiple pages, when use case invoked sequentially, then results accumulate correctly`() =
        runTest {
            val page1 = PaginatedResult(
                data = listOf(
                    LeagueList(league = League("La Liga", "Spain", 1, 38), teams = emptyList())
                ),
                totalPages = 2
            )
            val page2 = PaginatedResult(
                data = listOf(
                    LeagueList(league = League("Premier League", "England", 2, 38), teams = emptyList())
                ),
                totalPages = 2
            )

            coEvery { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) } returns Result.Success(page1)
            coEvery { repository.getLeagues(2, 10, LeagueListSortingStrategy.None) } returns Result.Success(page2)

            val result1 = getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)
            val result2 = getLeagueListUseCase(2, 10, LeagueListSortingStrategy.None)

            result1 shouldBe Result.Success(page1)
            result2 shouldBe Result.Success(page2)
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
            coVerify(exactly = 1) { repository.getLeagues(2, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository throws exception, when use case invoked, then exception propagates`() =
        runTest {
            val exception = RuntimeException("Unexpected")
            coEvery { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) } throws exception

            val thrown = shouldThrow<RuntimeException> {
                getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)
            }

            thrown shouldBe exception
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository returns leagues with players, when sorting by ranking ascending, then result is sorted with players`() =
        runTest {
            val sortedAsc = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = League(
                            name = "A League",
                            country = "CountryA",
                            rank = 1,
                            totalMatches = 38
                        ),
                        teams = listOf(
                            Team(
                                name = "TeamA1",
                                rank = 1,
                                players = listOf(Player(name = "PlayerA1", totalGoal = 10))
                            ),
                            Team(
                                name = "TeamA2",
                                rank = 2,
                                players = listOf(Player(name = "PlayerA2", totalGoal = 12))
                            )
                        )
                    ),
                    LeagueList(
                        league = League(
                            name = "Z League",
                            country = "CountryZ",
                            rank = 2,
                            totalMatches = 38
                        ),
                        teams = listOf(
                            Team(
                                name = "TeamZ1",
                                rank = 1,
                                players = listOf(Player("PlayerZ1", totalGoal = 15))
                            )
                        )
                    )
                ),
                totalPages = 1
            )

            coEvery {
                repository.getLeagues(
                    1,
                    10,
                    LeagueListSortingStrategy.ByLeagueRanking(LeagueListSortingStrategy.Direction.ASCENDING)
                )
            } returns Result.Success(sortedAsc)

            val result = getLeagueListUseCase(
                page = 1,
                limit = 10,
                sortingStrategy = LeagueListSortingStrategy.ByLeagueRanking(LeagueListSortingStrategy.Direction.ASCENDING)
            ) as Result.Success

            result.data.data.map { it.league.name } shouldBe listOf("A League", "Z League")

            val firstLeagueTeams = result.data.data[0].teams
            firstLeagueTeams.size shouldBe 2
            firstLeagueTeams[0].name shouldBe "TeamA1"
            firstLeagueTeams[0].players[0].name shouldBe "PlayerA1"
            firstLeagueTeams[0].players[0].totalGoal shouldBe 10
            firstLeagueTeams[1].name shouldBe "TeamA2"
            firstLeagueTeams[1].players[0].name shouldBe "PlayerA2"
            firstLeagueTeams[1].players[0].totalGoal shouldBe 12

            val secondLeagueTeams = result.data.data[1].teams
            secondLeagueTeams.size shouldBe 1
            secondLeagueTeams[0].name shouldBe "TeamZ1"
            secondLeagueTeams[0].players[0].name shouldBe "PlayerZ1"
            secondLeagueTeams[0].players[0].totalGoal shouldBe 15

            coVerify(exactly = 1) {
                repository.getLeagues(
                    1,
                    10,
                    LeagueListSortingStrategy.ByLeagueRanking(LeagueListSortingStrategy.Direction.ASCENDING)
                )
            }
        }
}
