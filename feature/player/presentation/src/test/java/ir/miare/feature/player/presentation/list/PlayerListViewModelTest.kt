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
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team
import ir.miare.feature.player.domain.usecase.GetLeagueListUseCase
import ir.miare.feature.player.presentation.MainCoroutineExtension
import ir.miare.feature.player.presentation.list.LeagueFakeGenerator.league
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
        fun `when load next page succeeds, should emit InitialLoading then Loaded with players`() =
            runTest {
                val expectedData = PaginatedResult(
                    data = listOf(
                        LeagueList(
                            league = league("La Liga", "Spain", 1, 38),
                            teams = listOf(
                                Team(
                                    name = "Real Madrid",
                                    rank = 1,
                                    players = listOf(Player("Jude Bellingham", totalGoal = 19))
                                ),
                                Team(
                                    name = "Barcelona",
                                    rank = 2,
                                    players = listOf(Player("Robert Lewandowski", totalGoal = 15))
                                )
                            )
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
                    val loadedState = awaitItem().paginatedLeagues as PaginatedState.Loaded

                    // Assert league
                    loadedState.data[0].league.name shouldBe "La Liga"
                    loadedState.data[0].league.country shouldBe "Spain"

                    // Assert teams and players
                    val teams = loadedState.data[0].teams
                    teams.map { it.name } shouldBe listOf("Real Madrid", "Barcelona")
                    teams[0].players[0].name shouldBe "Jude Bellingham"
                    teams[0].players[0].totalGoal shouldBe 19
                    teams[1].players[0].name shouldBe "Robert Lewandowski"
                    teams[1].players[0].totalGoal shouldBe 15
                }

                coVerify { getLeagueListUseCase(1, 10, viewModel.uiState.value.sortingStrategy) }
            }

        @Test
        fun `should load multiple pages and accumulate players`() = runTest {
            val firstPage = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = league("La Liga"),
                        teams = listOf(
                            Team("Team1", 1, listOf(Player("Player1", totalGoal = 10)))
                        )
                    )
                ),
                totalPages = 2
            )
            val secondPage = PaginatedResult(
                data = listOf(
                    LeagueList(
                        league = league("Premier League"),
                        teams = listOf(
                            Team("Team2", 2, listOf(Player("Player2", totalGoal = 7)))
                        )
                    )
                ),
                totalPages = 2
            )

            coEvery {
                getLeagueListUseCase(1, 10, viewModel.uiState.value.sortingStrategy)
            } returns Result.Success(firstPage)
            coEvery {
                getLeagueListUseCase(2, 10, viewModel.uiState.value.sortingStrategy)
            } returns Result.Success(secondPage)

            viewModel.uiState.test {
                awaitItem() // NotLoaded

                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem() // InitialLoading
                val loadedFirst = awaitItem().paginatedLeagues as PaginatedState.Loaded
                loadedFirst.data.size shouldBe 1
                loadedFirst.data[0].teams[0].players[0].name shouldBe "Player1"

                viewModel.onAction(PlayerListAction.LoadNextPage)
                awaitItem() // LoadingMore
                val loadedCombined = awaitItem().paginatedLeagues as PaginatedState.Loaded
                loadedCombined.data.size shouldBe 2
                loadedCombined.data[1].teams[0].players[0].name shouldBe "Player2"
            }
        }
    }

    @Nested
    inner class LoadNextPageFailureTests {
        @Test
        fun `when next page fails after first page, should emit LoadingMore then Error`() =
            runTest {
                val firstPage = PaginatedResult(
                    data = listOf(
                        LeagueList(
                            league = league("La Liga", "Spain", 1, 38),
                            teams = listOf(Team("Team1", 1, listOf(Player("Player1", totalGoal = 10))))
                        )
                    ),
                    totalPages = 2
                )

                coEvery {
                    getLeagueListUseCase(1, 10, viewModel.uiState.value.sortingStrategy)
                } returns Result.Success(firstPage)
                coEvery {
                    getLeagueListUseCase(2, 10, viewModel.uiState.value.sortingStrategy)
                } returns Result.Failure(DataError.Network.NO_INTERNET)

                viewModel.uiState.test {
                    awaitItem() // NotLoaded
                    viewModel.onAction(PlayerListAction.LoadNextPage)
                    awaitItem() // InitialLoading
                    awaitItem() // Loaded first page
                    viewModel.onAction(PlayerListAction.LoadNextPage)
                    awaitItem() // LoadingMore
                    awaitItem().paginatedLeagues shouldBe PaginatedState.Error(DataError.Network.NO_INTERNET)
                }
            }
    }

    @Nested
    inner class ChangeSortingStrategyTests {

        @Test
        fun `given ChangeSortingStrategy, when action called, then updates sortingStrategy and refreshes`() =
            runTest {
                val unsortedLeagues = PaginatedResult(
                    data = listOf(
                        LeagueList(
                            league("Z League", "CountryZ", 2, 38),
                            teams = listOf(Team("TeamZ", 2, listOf(Player("PlayerZ", totalGoal = 8))))
                        ),
                        LeagueList(
                            league("A League", "CountryA", 1, 38),
                            teams = listOf(Team("TeamA", 1, listOf(Player("PlayerA", totalGoal = 12))))
                        )
                    ),
                    totalPages = 1
                )

                val sortedLeagues = PaginatedResult(
                    data = listOf(
                        LeagueList(
                            league("A League", "CountryA", 1, 38),
                            teams = listOf(Team("TeamA", 1, listOf(Player("PlayerA", totalGoal = 12))))
                        ),
                        LeagueList(
                            league("Z League", "CountryZ", 2, 38),
                            teams = listOf(Team("TeamZ", 2, listOf(Player("PlayerZ", totalGoal = 8))))
                        )
                    ),
                    totalPages = 1
                )

                val newStrategy = LeagueListSortingStrategy.ByLeagueRanking(
                    LeagueListSortingStrategy.Direction.ASCENDING
                )

                // Mock use case
                coEvery {
                    getLeagueListUseCase(page = 1, limit = 10, sortingStrategy = LeagueListSortingStrategy.None)
                } returns Result.Success(unsortedLeagues)
                coEvery {
                    getLeagueListUseCase(page = 1, limit = 10, sortingStrategy = newStrategy)
                } returns Result.Success(sortedLeagues)

                viewModel.uiState.test {
                    // Initial NotLoaded
                    awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded

                    // Load first page (unsorted)
                    viewModel.onAction(PlayerListAction.LoadNextPage)
                    awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                    val firstLoaded = awaitItem()
                    firstLoaded.paginatedLeagues.data?.map { it.league.name } shouldBe listOf("Z League", "A League")

                    // Assert unsorted players
                    val firstPlayers = firstLoaded.paginatedLeagues.data?.map { it.teams }?.flatten()?.map { it.players }?.flatten()
                    firstPlayers?.map { it.name } shouldBe listOf("PlayerZ", "PlayerA")
                    firstPlayers?.map { it.totalGoal } shouldBe listOf(8, 12)

                    // Change sorting strategy
                    viewModel.onAction(PlayerListAction.ChangeSortingStrategy(newStrategy))
                    advanceUntilIdle()

                    awaitItem().paginatedLeagues shouldBe PaginatedState.NotLoaded
                    awaitItem().paginatedLeagues shouldBe PaginatedState.InitialLoading
                    val sortedLoaded = awaitItem()
                    val sortedPlayers = sortedLoaded.paginatedLeagues.data?.map { it.teams }?.flatten()?.map { it.players }?.flatten()
                    sortedLoaded.paginatedLeagues.data?.map { it.league.name } shouldBe listOf("A League", "Z League")
                    sortedPlayers?.map { it.name } shouldBe listOf("PlayerA", "PlayerZ")
                    sortedPlayers?.map { it.totalGoal } shouldBe listOf(12, 8)
                }

                coVerify { getLeagueListUseCase(1, 10, newStrategy) }
                viewModel.uiState.value.sortingStrategy shouldBe newStrategy
            }
    }
}
