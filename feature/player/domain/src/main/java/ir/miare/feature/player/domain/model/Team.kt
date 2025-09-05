package ir.miare.feature.player.domain.model

data class Team(
    val name: String,
    val rank: Int,
    val players: List<Player>
)