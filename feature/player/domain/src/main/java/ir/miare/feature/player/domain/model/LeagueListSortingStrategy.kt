package ir.miare.feature.player.domain.model

sealed class LeagueListSortingStrategy {

    enum class Direction { ASCENDING, DESCENDING }

    data object None : LeagueListSortingStrategy()
    data class ByLeagueName(val direction: Direction = Direction.ASCENDING) : LeagueListSortingStrategy()
    data class ByCountry(val direction: Direction = Direction.ASCENDING) : LeagueListSortingStrategy()
    data class ByLeagueRanking(val direction: Direction = Direction.ASCENDING) : LeagueListSortingStrategy()
    data class ByMostGoals(val direction: Direction = Direction.DESCENDING) : LeagueListSortingStrategy()
    data class ByAverageGoalsPerMatch(val direction: Direction = Direction.DESCENDING) :
        LeagueListSortingStrategy()

    fun displayName(): String = when (this) {
        None -> "Default"
        is ByLeagueName -> "League Name"
        is ByCountry -> "Country"
        is ByLeagueRanking -> "League Ranking"
        is ByMostGoals -> "Most Goals"
        is ByAverageGoalsPerMatch -> "Average Goals per Match"
    }

    companion object {
        fun values(): List<LeagueListSortingStrategy> = listOf(
            None,
            ByLeagueName(),
            ByCountry(),
            ByLeagueRanking(),
            ByMostGoals(),
            ByAverageGoalsPerMatch()
        )
    }
}
