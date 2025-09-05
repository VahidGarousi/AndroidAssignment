package ir.miare.feature.player.data.repository

import ir.miare.core.common.util.DataError
import ir.miare.core.common.util.Result
import ir.miare.core.domain.model.PaginatedResult
import ir.miare.core.network.retrofit.safeCall
import ir.miare.feature.player.data.remote.api.retrofit.PlayerApiService
import ir.miare.feature.player.domain.model.League
import ir.miare.feature.player.domain.model.LeagueList
import ir.miare.feature.player.domain.model.Player
import ir.miare.feature.player.domain.model.Team
import ir.miare.feature.player.domain.repository.PlayerRepository
import javax.inject.Inject


class PlayerRepositoryImpl @Inject constructor(
    private val apiService: PlayerApiService
) : PlayerRepository {
    override suspend fun getLeagues(
        page: Int,
        limit: Int
    ): Result<PaginatedResult<League>, DataError.Network> {
        return safeCall(mapError = { it }) {
            // 1. Fetch from API
            val leagues = apiService.getLeagueList()

            // 2. Map DTO → Domain
            val domainLeagues = leagues.map { leagueDto ->
                LeagueList(
                    league = League(
                        name = leagueDto.league.name,
                        country = leagueDto.league.country,
                        rank = leagueDto.league.rank,
                        totalMatches = leagueDto.league.totalMatches
                    ),
                    players = leagueDto.players.map { playerDto ->
                        Player(
                            name = playerDto.name,
                            totalGoal = playerDto.totalGoal,
                            team = Team(
                                name = playerDto.team.name,
                                rank = playerDto.team.rank
                            )
                        )
                    }
                )
            }

            // 3. Extract only Leagues
            val allLeagues = domainLeagues.map { it.league }

            // 4. Sort leagues (for example, by rank ascending)
            val sortedLeagues = allLeagues.sortedBy { it.rank }

            // 5. Pagination
            val fromIndex = (page - 1) * limit
            val toIndex = minOf(fromIndex + limit, sortedLeagues.size)

            val paginatedLeagues = if (fromIndex < sortedLeagues.size) {
                sortedLeagues.subList(fromIndex, toIndex)
            } else emptyList()

            // 6. Total pages
            val totalPages = if (limit == 0) 0 else
                (sortedLeagues.size + limit - 1) / limit

            // 7. Return PaginatedResult
            PaginatedResult(
                data = paginatedLeagues,
                totalPages = totalPages
            )
        }
    }
}