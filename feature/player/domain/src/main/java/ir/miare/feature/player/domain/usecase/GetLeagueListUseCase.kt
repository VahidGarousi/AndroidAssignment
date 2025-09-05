package ir.miare.feature.player.domain.usecase

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.repository.PlayerRepository
import javax.inject.Inject

class GetLeagueListUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(
        page: Int,
        limit: Int,
    ): Result<PaginatedResult<League>, DataError.Network> {
        return repository.getLeagues(
            page = page,
            limit = limit
        )
    }
}