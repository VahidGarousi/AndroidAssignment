package ir.miare.feature.player.domain.model

data class League(
    val country: String,
    val name: String,
    val rank: Int,
    val totalMatches: Int
)