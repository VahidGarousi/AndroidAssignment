package ir.miare.feature.player.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    @SerialName("name")
    val name: String,
    @SerialName("team")
    val team: TeamDto,
    @SerialName("total_goal")
    val totalGoal: Int
)