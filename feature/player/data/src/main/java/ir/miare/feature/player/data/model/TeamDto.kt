package ir.miare.feature.player.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    @SerialName("name")
    val name: String,
    @SerialName("rank")
    val rank: Int
)