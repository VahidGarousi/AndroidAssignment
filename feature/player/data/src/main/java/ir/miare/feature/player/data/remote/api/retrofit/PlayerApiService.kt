package ir.miare.feature.player.data.remote.api.retrofit

import ir.miare.feature.player.data.model.LeagueListItemDto
import retrofit2.http.GET

interface PlayerApiService {
    @GET("list")
    suspend fun getLeagueList(): List<LeagueListItemDto>
}