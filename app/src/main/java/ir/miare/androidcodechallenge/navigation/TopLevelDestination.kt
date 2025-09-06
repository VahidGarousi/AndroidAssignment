package ir.miare.androidcodechallenge.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ir.miare.core.designsystem.MiareIcons
import ir.miare.feature.player.presentation.navigation.PlayerBaseRoute
import ir.miare.feature.player.presentation.R as playerR
import kotlin.reflect.KClass
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
//    EXPLORE(
//        selectedIcon = ModJameIcons.Explore,
//        unselectedIcon = ModJameIcons.Explore,
//        iconTextId = R.string.feature_explore_presentation_title,
//        titleTextId = exploreR.string.feature_explore_presentation_title,
//        route = ExploreRoute::class,
//        baseRoute = ExploreBaseRoute::class,
//    ),
    LEAGUES(
        selectedIcon = MiareIcons.Studio,
        unselectedIcon = MiareIcons.Studio,
        iconTextId = playerR.string.feature_player_presentation_title,
        titleTextId = playerR.string.feature_player_presentation_title,
        route = PlayerBaseRoute::class,
    ),
//    PROFILE(
//        selectedIcon = ModJameIcons.Profile,
//        unselectedIcon = ModJameIcons.Profile,
//        iconTextId = profileR.string.feature_profile_presentation_title,
//        titleTextId = profileR.string.feature_profile_presentation_title,
//        route = ProfileRoute::class,
//    ),
}