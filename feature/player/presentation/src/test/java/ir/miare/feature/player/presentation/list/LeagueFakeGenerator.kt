package ir.miare.feature.player.presentation.list

import ir.miare.feature.player.domain.model.League

object LeagueFakeGenerator {
    fun league(
        name: String = "La Liga",
        country: String = "Spain",
        rank: Int = 1,
        matches: Int = 38
    ) = League(
        country = country,
        name = name,
        rank = rank,
        totalMatches = matches
    )
}