package ir.miare.feature.player.presentation.list

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.core.ui.paginator.state.PaginatedState
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.usecase.GetLeagueListUseCase
import ir.miare.feature.player.presentation.MainCoroutineExtension
import ir.miare.feature.player.presentation.list.LeagueFakeGenerator.league
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainCoroutineExtension::class)
internal class PlayerListViewModelTest {
    private lateinit var getLeagueListUseCase: GetLeagueListUseCase
    private lateinit var viewModel: PlayerListViewModel

    @BeforeEach
    fun setup() {
        getLeagueListUseCase = mockk(relaxed = true)
        viewModel = PlayerListViewModel(
            getLeagueListUseCase = getLeagueListUseCase
        )
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
                data = listOf(
                    LeagueList(
                        league = league("La Liga", "Spain", 1, 38),
                        players = emptyList()
                    )
                ),
                totalPages = 1
            )

            coEvery {
                getLeagueListUseCase(
                    page = 1,
                    limit = 10,
                    sortingStrategy = viewModel.uiState.value.sortingStrategy
                )
            } returns Result.Success(expectedData)

            viewModel.uiState.test {
                awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = expectedData.data.toImmutableList(),
                    isEndReached = true
                )
            }

            coVerify {
                getLeagueListUseCase(
                    page = 1,
                    limit = 10,
                    sortingStrategy = viewModel.uiState.value.sortingStrategy
                )
            }
        }

        @Test
        fun `should load first page then load more and accumulate data`() = runTest {
            val firstPage = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = league("La Liga"),
                        players = emptyList()
                    )
                ),
                totalPages = 2
            )
            val secondPage = PaginatedResult(
                data = (1..19).map {
                    LeagueList(
                        league = league("Player $it"),
                        players = emptyList()
                    )
                },
                totalPages = 2
            )

            coEvery {
                getLeagueListUseCase(
                    1,
                    10,
                    sortingStrategy = viewModel.uiState.value.sortingStrategy
                )
            } returns Result.Success(firstPage)
            coEvery {
                getLeagueListUseCase(
                    2,
                    10,
                    sortingStrategy = viewModel.uiState.value.sortingStrategy
                )
            } returns Result.Success(secondPage)

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
            coEvery {
                getLeagueListUseCase(
                    page = 1,
                    limit = 10,
                    sortingStrategy = viewModel.uiState.value.sortingStrategy
                )
            } returns Result.Failure(DataError.Network.NO_INTERNET)

            viewModel.uiState.test {
                awaitItem() // NotLoaded
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem() // InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Error(DataError.Network.NO_INTERNET)
            }
        }

        @Test
        fun `when next page fails after first page, should emit LoadingMore then Error`() =
            runTest {
                val firstPage = PaginatedResult(
                    data = listOf(
                        LeagueList(
                            league = league("La Liga", "Spain", 1, 38),
                            players = emptyList()
                        )
                    ),
                    totalPages = 2
                )

                coEvery {
                    getLeagueListUseCase(
                        1,
                        10,
                        sortingStrategy = viewModel.uiState.value.sortingStrategy
                    )
                } returns Result.Success(firstPage)
                coEvery {
                    getLeagueListUseCase(
                        2,
                        10,
                        sortingStrategy = viewModel.uiState.value.sortingStrategy
                    )
                } returns Result.Failure(DataError.Network.NO_INTERNET)

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
    inner class ChangeSortingStrategyTests2 {

        @Test
        fun `given ChangeSortingStrategy, when action called, then updates sortingStrategy and refreshes`() =
            runTest {
                val unsortedLeagues = PaginatedResult(
                    data = listOf(
                        LeagueList(league("Z League", "CountryZ", 2, 38), players = emptyList()),
                        LeagueList(league("A League", "CountryA", 1, 38), players = emptyList())
                    ),
                    totalPages = 1
                )

                val sortedLeagues = PaginatedResult(
                    data = listOf(
                        LeagueList(league("A League", "CountryA", 1, 38), players = emptyList()),
                        LeagueList(league("Z League", "CountryZ", 2, 38), players = emptyList())
                    ),
                    totalPages = 1
                )

                val newStrategy = LeagueListSortingStrategy.ByLeagueRanking(
                    LeagueListSortingStrategy.Direction.ASCENDING
                )

                // Mock use case
                coEvery {
                    getLeagueListUseCase(
                        1,
                        10,
                        LeagueListSortingStrategy.None
                    )
                } returns Result.Success(unsortedLeagues)
                coEvery { getLeagueListUseCase(1, 10, newStrategy) } returns Result.Success(
                    sortedLeagues
                )

                viewModel.uiState.test {
                    // Initial NotLoaded state
                    awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded

                    // Trigger first page load
                    viewModel.onAction(PlayerListAction.LoadNextPage)
                    awaitItem() // InitialLoading
                    val firstLoaded = awaitItem() // Loaded with unsorted leagues
                    firstLoaded.paginatedLeagues.data?.map { it.league.name } shouldBe listOf(
                        "Z League", "A League"
                    )

                    // Trigger sorting change
                    viewModel.onAction(PlayerListAction.ChangeSortingStrategy(newStrategy))
                    advanceUntilIdle() // wait for refresh coroutine

                    // Skip any intermediate states emitted during refresh
                    var sortedLoaded: PlayerListState
                    do {
                        sortedLoaded = awaitItem()
                    } while (sortedLoaded.paginatedLeagues.data?.map { it.league.name }
                            ?.firstOrNull() != "A League")

                    // Now assert sorted order
                    sortedLoaded.paginatedLeagues.data?.map { it.league.name } shouldBe listOf(
                        "A League", "Z League"
                    )
                }


                coVerify { getLeagueListUseCase(1, 10, newStrategy) }
                viewModel.uiState.value.sortingStrategy shouldBe newStrategy
            }
    }


    @Nested
    inner class ChangeSortingStrategyTests {

        @Test
        fun `given ChangeSortingStrategy, when action called, then updates sortingStrategy and refreshes`() =
            runTest {
                val unsortedLeagues = PaginatedResult(
                    data = listOf(
                        LeagueList(league("Z League", "CountryZ", 2, 38), players = emptyList()),
                        LeagueList(league("A League", "CountryA", 1, 38), players = emptyList())
                    ),
                    totalPages = 1
                )

                val sortedLeagues = PaginatedResult(
                    data = listOf(
                        LeagueList(league("A League", "CountryA", 1, 38), players = emptyList()),
                        LeagueList(league("Z League", "CountryZ", 2, 38), players = emptyList())
                    ),
                    totalPages = 1
                )

                val newStrategy = LeagueListSortingStrategy.ByLeagueRanking(
                    LeagueListSortingStrategy.Direction.ASCENDING
                )

                // Mock use case
                coEvery {
                    getLeagueListUseCase(
                        1,
                        10,
                        LeagueListSortingStrategy.None
                    )
                } returns Result.Success(unsortedLeagues)
                coEvery { getLeagueListUseCase(1, 10, newStrategy) } returns Result.Success(
                    sortedLeagues
                )

                viewModel.uiState.test {
                    // Initial NotLoaded
                    awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded

                    // Load first page (unsorted)
                    viewModel.onAction(PlayerListAction.LoadNextPage)
                    awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                    val firstLoaded = awaitItem()
                    firstLoaded.paginatedLeagues.data?.map {
                        it.league.name
                    } shouldBe listOf(
                        "Z League",
                        "A League"
                    )
                    // Change sorting strategy
                    viewModel.onAction(PlayerListAction.ChangeSortingStrategy(newStrategy))
                    advanceUntilIdle()
                    awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded
                    awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                    val sortedLoaded = awaitItem()
                    sortedLoaded.paginatedLeagues.data?.map {
                        it.league.name
                    } shouldBe listOf("A League", "Z League")
                }

                coVerify { getLeagueListUseCase(1, 10, newStrategy) }
                viewModel.uiState.value.sortingStrategy shouldBe newStrategy
            }
    }


    @Nested
    inner class EmptyDataTests {
        @Test
        fun `when API returns empty list, should emit Loaded with empty data`() = runTest {
            val emptyPage = PaginatedResult(
                data = emptyList<LeagueList>(),
                totalPages = 1
            )

            coEvery {
                getLeagueListUseCase(
                    1,
                    10,
                    sortingStrategy = viewModel.uiState.value.sortingStrategy
                )
            } returns Result.Success(emptyPage)

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                awaitItem().paginatedLeagues shouldBe PaginatedState.Loaded(
                    data = emptyList<LeagueList>().toImmutableList(),
                    isEndReached = true
                )
            }
        }
    }

}