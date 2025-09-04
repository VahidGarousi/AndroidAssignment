package ir.miare.feature.player.presentation.list

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.domain.GetLeagueListUseCase
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.presentation.LoadableData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PlayerListViewModelTest {
    private lateinit var getLeagueListUseCase: GetLeagueListUseCase
    private lateinit var viewModel: PlayerListViewModel

    @BeforeEach
    fun setup() {
        getLeagueListUseCase = mockk(relaxed = true)
    }

    @Test
    fun `given ViewModel is ready for initialization, when initialized, then uiState is default`() =
        runTest {
            // Arrange
            viewModel = PlayerListViewModel(
                getLeagueListUseCase = getLeagueListUseCase
            )
            // Assert
            viewModel.uiState.value.paginatedLeagues shouldBe LoadableData.NotLoaded
        }

    @Test
    fun `given viewmodel initialized and we have valid players, when init block called, then players loaded`() =
        runTest {
            val expectedData =  PaginatedResult<League>(
                data = listOf(),
                totalPages = 10
            )
            coEvery {
                getLeagueListUseCase(
                    page = any(),
                    limit = any()
                )
            } coAnswers {
                Result.Success(expectedData)
            }
            viewModel = PlayerListViewModel(
                getLeagueListUseCase = getLeagueListUseCase
            )
            viewModel.uiState.test {
                awaitItem().paginatedLeagues shouldBe LoadableData.NotLoaded
                awaitItem().paginatedLeagues shouldBe LoadableData.Loading
                awaitItem().paginatedLeagues shouldBe LoadableData.Loaded(expectedData.data)
            }
        }
}