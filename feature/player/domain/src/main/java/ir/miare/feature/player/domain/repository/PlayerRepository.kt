package ir.miare.feature.player.domain.repository

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy

interface PlayerRepository {
    suspend fun getLeagues(
        page: Int,
        limit: Int,
        sortingStrategy: LeagueListSortingStrategy
    ): Result<PaginatedResult<LeagueList>, DataError.Network>
}