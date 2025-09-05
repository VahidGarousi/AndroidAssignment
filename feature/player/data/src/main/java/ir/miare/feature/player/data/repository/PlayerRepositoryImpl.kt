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
        apiService.getLeagueList()
            .map { it.toDomain() }
            .applySorting(sortingStrategy)
            .let { sorted ->
                val paginated = sorted.paginate(page, limit)
                PaginatedResult(
                    data = paginated,
                    totalPages = sorted.totalPages(limit)
                )
            }
    }



    private fun LeagueListItemDto.toDomain(): LeagueList {
        val playersByTeam = players.groupBy { it.team.name to it.team.rank }

        val teams = playersByTeam.map { (teamKey, groupedPlayers) ->
            val (teamName, teamRank) = teamKey
            Team(
                name = teamName,
                rank = teamRank,
                players = groupedPlayers.map { it.toDomainPlayer() }
            )
        }

        return LeagueList(
            league = League(
                name = league.name,
                country = league.country,
                rank = league.rank,
                totalMatches = league.totalMatches
            ),
            teams = teams
        )
    }

    private fun PlayerDto.toDomainPlayer() = Player(
        name = name,
        totalGoal = totalGoal
    )
}


// -------------------- Sorting & Pagination Extensions --------------------

fun List<LeagueList>.applySorting(strategy: LeagueListSortingStrategy): List<LeagueList> =
    when (strategy) {
        LeagueListSortingStrategy.None -> this
        is LeagueListSortingStrategy.ByLeagueName -> sortByField({ it.league.name }, strategy.direction)
        is LeagueListSortingStrategy.ByCountry -> sortByField({ it.league.country }, strategy.direction)
        is LeagueListSortingStrategy.ByLeagueRanking -> sortByField({ it.league.rank }, strategy.direction)
        is LeagueListSortingStrategy.ByMostGoals -> sortByField(
            { it.teams.sumOf { t -> t.players.sumOf(Player::totalGoal) } },
            strategy.direction
        )
        is LeagueListSortingStrategy.ByAverageGoalsPerMatch -> sortByField(
            {
                val totalGoals = it.teams.sumOf { t -> t.players.sumOf(Player::totalGoal) }
                val matches = it.league.totalMatches.takeIf { m -> m > 0 } ?: 1
                totalGoals.toDouble() / matches
            },
            strategy.direction
        )
    }

private fun <T : Comparable<T>> List<LeagueList>.sortByField(
    selector: (LeagueList) -> T,
    direction: LeagueListSortingStrategy.Direction
): List<LeagueList> =
    if (direction == LeagueListSortingStrategy.Direction.ASCENDING) sortedBy(selector)
    else sortedByDescending(selector)

private fun List<LeagueList>.paginate(page: Int, limit: Int): List<LeagueList> {
    val fromIndex = (page - 1) * limit
    val toIndex = minOf(fromIndex + limit, size)
    return if (fromIndex < size) subList(fromIndex, toIndex) else emptyList()
}

private fun List<LeagueList>.totalPages(limit: Int): Int =
    if (limit <= 0) 0 else (size + limit - 1) / limit