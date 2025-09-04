package ir.miare.feature.player.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueListItemDto(
    @SerialName("league")
    val league: LeagueDto,
    @SerialName("players")
    val players: List<PlayerDto>
)