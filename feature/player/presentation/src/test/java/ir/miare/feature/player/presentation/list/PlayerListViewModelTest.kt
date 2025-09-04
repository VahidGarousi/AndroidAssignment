package ir.miare.feature.player.presentation.list

import androidx.compose.runtime.mutableStateListOf
import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import ir.miare.feature.player.presentation.LoadableData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class PlayerListViewModelTest {
    @Test
    fun `given ViewModel is ready for initialization, when initialized, then uiState is default`() =
        runTest {
            // Arrange
            val viewModel = PlayerListViewModel()
            // Assert
            viewModel.uiState.value.players shouldBe LoadableData.NotLoaded
        }

    @Test
    fun `given viewmodel initialized and we have valid players, when init block called, then players loaded`() =
        runTest {
            val viewModel = PlayerListViewModel()
            viewModel.uiState.test {
                awaitItem().players shouldBe LoadableData.NotLoaded
                awaitItem().players shouldBe LoadableData.Loading
                awaitItem().players shouldBe LoadableData.Loaded(mutableStateListOf())
            }
        }
}