package ir.miare.feature.player.presentation.list

import ir.miare.feature.player.domain.model.League

object LeagueFakeGenerator {
    fun league(
        name: String = "La Liga",
        country: String = "Spain",
        rank: Int = 1,
        matches: Int = 38
    ) = League(
        country = name,
        name = country,
        rank = rank,
        totalMatches = matches
    )
}