package ir.miare.feature.player.presentation.list

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.GetLeagueListUseCase
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.presentation.list.LeagueFakeGenerator.league
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerListViewModelTest {

    private lateinit var getLeagueListUseCase: GetLeagueListUseCase
    private lateinit var viewModel: PlayerListViewModel

    @BeforeEach
    fun setup() {
        getLeagueListUseCase = mockk(relaxed = true)
        viewModel = PlayerListViewModel(getLeagueListUseCase = getLeagueListUseCase)
    }

    @Nested
    inner class DefaultStateTests {
        @Test
        fun `default state is NotLoaded`() = runTest {
            viewModel.uiState.value.paginatedLeagues shouldBe PaginatedState.NotLoaded
        }
    }

    @Nested
    inner class LoadNextPageSuccessTests {
        @Test
        fun `when load next page succeeds, should emit InitialLoading then Loaded`() = runTest {
            val expectedData = PaginatedResult(
                data = listOf(league("La Liga", "Spain", 1, 38)),
                totalPages = 1
            )
            coEvery { getLeagueListUseCase(1, any()) } returns Result.Success(expectedData)

            viewModel.uiState.test {
                awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = expectedData.data.toImmutableList(),
                    isEndReached = true
                )
            }
        }

        @Test
        fun `should load first page then load more and accumulate data`() = runTest {
            val firstPage = PaginatedResult(
                data = listOf(league("La Liga")),
                totalPages = 2
            )
            val secondPage = PaginatedResult(
                data = (1..19).map { league("Player $it") },
                totalPages = 2
            )

            coEvery { getLeagueListUseCase(1, any()) } returns Result.Success(firstPage)
            coEvery { getLeagueListUseCase(2, any()) } returns Result.Success(secondPage)

            viewModel.uiState.test {
                awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded

                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = firstPage.data.toImmutableList(),
                    isEndReached = false
                )

                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.LoadingMore(firstPage.data.toImmutableList())
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = (firstPage.data + secondPage.data).toImmutableList(),
                    isEndReached = true
                )
            }
        }
    }

    @Nested
    inner class LoadNextPageFailureTests {
        @Test
        fun `when load next page fails, should emit InitialLoading then Error`() = runTest {
            coEvery { getLeagueListUseCase(any(), any()) } returns Result.Failure(DataError.Network.NO_INTERNET)

            viewModel.uiState.test {
                awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Error(DataError.Network.NO_INTERNET)
            }
        }

        @Test
        fun `when next page fails after first page, should emit LoadingMore then Error`() = runTest {
            val firstPage = PaginatedResult(
                data = listOf(league("La Liga", "Spain", 1, 38)),
                totalPages = 2
            )
            coEvery { getLeagueListUseCase(1, any()) } returns Result.Success(firstPage)
            coEvery { getLeagueListUseCase(2, any()) } returns Result.Failure(DataError.Network.NO_INTERNET)

            viewModel.uiState.test {
                awaitItem() // NotLoaded
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem() // InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = firstPage.data.toImmutableList(),
                    isEndReached = false
                )

                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.LoadingMore(firstPage.data.toImmutableList())
                awaitItem().paginatedLeagues shouldBe PaginatedState.Error(DataError.Network.NO_INTERNET)
            }
        }
    }

    @Nested
    inner class EmptyDataTests {
        @Test
        fun `when API returns empty list, should emit Loaded with empty data`() = runTest {
            val emptyPage = PaginatedResult(
                data = emptyList<League>(),
                totalPages = 1
            )
            coEvery { getLeagueListUseCase(1, any()) } returns Result.Success(emptyPage)

            viewModel.uiState.test {
                awaitItem() // NotLoaded
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = emptyList<League>().toImmutableList(),
                    isEndReached = true
                )
            }
        }
    }
}
