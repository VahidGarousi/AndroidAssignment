package ir.miare.feature.player.domain

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.feature.player.domain.model.Player

interface PlayerRepository {
    suspend fun getPlayers(): Result<List<Player>, DataError.Network>
}