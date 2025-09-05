package ir.miare.feature.player.data.repository

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.core.network.retrofit.safeCall
import ir.miare.feature.player.data.model.LeagueListItemDto
import ir.miare.feature.player.data.model.PlayerDto
import ir.miare.feature.player.data.remote.api.retrofit.PlayerApiService
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.LeagueListSortingStrategy
import ir.miare.feature.player.domain.model.Team
import ir.miare.feature.player.domain.repository.PlayerRepository
import javax.inject.Inject


class PlayerRepositoryImpl @Inject constructor(
    private val apiService: PlayerApiService
) : PlayerRepository {
    override suspend fun getLeagues(
        page: Int,
        limit: Int,
        sortingStrategy: LeagueListSortingStrategy
    ): Result<PaginatedResult<LeagueList>, DataError.Network> = safeCall(mapError = { it }) {
        val domainLeagues = apiService.getLeagueList().map { dto ->
            dto.toDomain()
        }
        // Apply sorting
        val sortedLeagues = domainLeagues.applySorting(sortingStrategy)

        // Apply pagination
        val paginated = sortedLeagues.paginate(page, limit)

        PaginatedResult(
            data = paginated,
            totalPages = totalPages(sortedLeagues.size, limit)
        )

    }


    // Convert DTO → Domain
    private fun LeagueListItemDto.toDomain(): LeagueList = LeagueList(
        league = League(
            name = league.name,
            country = league.country,
            rank = league.rank,
            totalMatches = league.totalMatches
        ),
        players = players.map { it.toDomain() }
    )

    private fun PlayerDto.toDomain(): Player = Player(
        name = name,
        totalGoal = totalGoal,
        team = Team(
            name = team.name,
            rank = team.rank
        )
    )
}


// -------------------- Sorting & Pagination Extensions --------------------

fun List<LeagueList>.applySorting(strategy: LeagueListSortingStrategy): List<LeagueList> = when (strategy) {
    LeagueListSortingStrategy.None -> this
    is LeagueListSortingStrategy.ByLeagueName -> sortByField({ it.league.name }, strategy.direction)
    is LeagueListSortingStrategy.ByCountry -> sortByField({ it.league.country }, strategy.direction)
    is LeagueListSortingStrategy.ByLeagueRanking -> sortByField({ it.league.rank }, strategy.direction)
    is LeagueListSortingStrategy.ByMostGoals -> sortByField(
        { it.players.sumOf { p -> p.totalGoal } },
        strategy.direction
    )

    is LeagueListSortingStrategy.ByAverageGoalsPerMatch -> sortByField(
        {
            val totalGoals = it.players.sumOf { p -> p.totalGoal }
            val matches = it.league.totalMatches.takeIf { m -> m > 0 } ?: 1
            totalGoals.toDouble() / matches
        }, strategy.direction
    )
}

private fun <T : Comparable<T>> List<LeagueList>.sortByField(
    selector: (LeagueList) -> T,
    direction: LeagueListSortingStrategy.Direction
): List<LeagueList> = if (direction == LeagueListSortingStrategy.Direction.ASCENDING) this.sortedBy(selector)
else this.sortedByDescending(selector)

private fun List<LeagueList>.paginate(page: Int, limit: Int): List<LeagueList> {
    val fromIndex = (page - 1) * limit
    val toIndex = minOf(fromIndex + limit, size)
    return if (fromIndex < size) subList(fromIndex, toIndex) else emptyList()
}

private fun totalPages(totalItems: Int, limit: Int): Int =
    if (limit == 0) 0 else (totalItems + limit - 1) / limit