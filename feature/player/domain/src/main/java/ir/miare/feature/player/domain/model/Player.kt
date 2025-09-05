package ir.miare.feature.player.domain.model

data class Player(
    val name: String,
    val team: Team,
    val totalGoal: Int
)