package ir.miare.feature.player.domain.repository

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.domain.model.League

interface PlayerRepository {
    suspend fun getLeagues(
        page: Int,
        limit: Int,
    ): Result<PaginatedResult<League>, DataError.Network>
}