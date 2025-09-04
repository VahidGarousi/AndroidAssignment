package ir.miare.feature.player.domain.model

data class PlayerResponse(
    val league: League,
    val players: List<Player>
)