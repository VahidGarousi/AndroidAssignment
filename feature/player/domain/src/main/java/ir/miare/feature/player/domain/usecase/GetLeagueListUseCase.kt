package ir.miare.feature.player.domain.usecase

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.repository.PlayerRepository
import javax.inject.Inject

class GetLeagueListUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(
        page: Int,
        limit: Int,
        sortingStrategy: LeagueListSortingStrategy
    ): Result<PaginatedResult<LeagueList>, DataError.Network> = repository.getLeagues(
        page = page,
        limit = limit,
        sortingStrategy = sortingStrategy
    )
}