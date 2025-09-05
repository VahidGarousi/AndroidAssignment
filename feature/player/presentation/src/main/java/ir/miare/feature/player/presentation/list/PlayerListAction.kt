package ir.miare.feature.player.presentation.list

sealed interface PlayerListAction {
    data object LoadNextPage : PlayerListAction
}