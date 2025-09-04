package ir.miare.feature.player.presentation.list

import androidx.compose.runtime.snapshots.SnapshotStateList
import ir.miare.feature.player.presentation.LoadableData
import ir.miare.feature.player.presentation.Player

data class PlayerListState(
    val players : LoadableData<SnapshotStateList<Player>> = LoadableData.NotLoaded
)
