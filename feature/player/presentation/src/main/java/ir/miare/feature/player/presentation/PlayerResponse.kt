package ir.miare.feature.player.presentation

data class PlayerResponse(
    val league: League,
    val players: List<Player>
)