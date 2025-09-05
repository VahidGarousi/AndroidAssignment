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
            // Arrange
            val expected = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = League("La Liga", "Spain", 1, 38),
                        players = emptyList()
                    )
                ),
                totalPages = 1
            )

            coEvery {
                repository.getLeagues(1, 10, LeagueListSortingStrategy.None)
            } returns Result.Success(expected)

            // Act
            val result = getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)

            // Assert
            result shouldBe Result.Success(expected)
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository returns network error, when use case is invoked, then result is failure`() =
        runTest {
            // Arrange
            val error = DataError.Network.NO_INTERNET
            coEvery {
                repository.getLeagues(2, 20, LeagueListSortingStrategy.None)
            } returns Result.Failure(error)

            // Act
            val result = getLeagueListUseCase(2, 20, LeagueListSortingStrategy.None)

            // Assert
            result shouldBe Result.Failure(error)
            coVerify(exactly = 1) { repository.getLeagues(2, 20, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository returns empty list, when use case is invoked, then result is success with empty data`() =
        runTest {
            // Arrange
            val emptyResult = PaginatedResult<LeagueList>(
                data = emptyList(),
                totalPages = 1
            )

            coEvery {
                repository.getLeagues(1, 10, LeagueListSortingStrategy.None)
            } returns Result.Success(emptyResult)

            // Act
            val result = getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)

            // Assert
            result shouldBe Result.Success(emptyResult)
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository called with multiple pages, when use case invoked sequentially, then results accumulate correctly`() =
        runTest {
            // Arrange
            val page1 = PaginatedResult(
                data = listOf(
                    LeagueList(league = League("La Liga", "Spain", 1, 38), players = emptyList())
                ),
                totalPages = 2
            )
            val page2 = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = League("Premier League", "England", 2, 38),
                        players = emptyList()
                    )
                ),
                totalPages = 2
            )

            coEvery {
                repository.getLeagues(1, 10, LeagueListSortingStrategy.None)
            } returns Result.Success(page1)
            coEvery {
                repository.getLeagues(2, 10, LeagueListSortingStrategy.None)
            } returns Result.Success(page2)

            // Act
            val result1 = getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)
            val result2 = getLeagueListUseCase(2, 10, LeagueListSortingStrategy.None)

            // Assert
            result1 shouldBe Result.Success(page1)
            result2 shouldBe Result.Success(page2)
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
            coVerify(exactly = 1) { repository.getLeagues(2, 10, LeagueListSortingStrategy.None) }
        }

    @Test
    fun `given repository throws exception, when use case invoked, then exception propagates`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Unexpected")
            coEvery {
                repository.getLeagues(1, 10, LeagueListSortingStrategy.None)
            } throws exception

            // Act & Assert
            val thrown = shouldThrow<RuntimeException> {
                getLeagueListUseCase(1, 10, LeagueListSortingStrategy.None)
            }

            // Assert exception details
            thrown shouldBe exception
            coVerify(exactly = 1) { repository.getLeagues(1, 10, LeagueListSortingStrategy.None) }
        }
}
