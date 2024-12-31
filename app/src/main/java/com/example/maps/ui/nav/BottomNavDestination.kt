package com.example.maps.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.example.maps.R

sealed class BottomNavDestination(
    route: String,
    @DrawableRes
    val iconId: Int
    ): Destination(route) {

    @Composable
    fun getIconVector() = ImageVector.vectorResource(iconId)

    object Home : BottomNavDestination(route = "home", iconId = R.drawable.ic_menu) {

        fun navigateToOnBoardingScreen(navController: NavController) {
            navController.navigate(OnBoardingDestination.route)
        }

        fun navigateToRunStats(navController: NavController) {
            navController.navigate(RunStats.route)
        }

        object RecentRun: Destination(route = "recent_run"){
            fun navigateToRunningHistoryScreen(navController: NavController) {
                navController.navigate(RunningHistory.route)
            }
        }

        object RunningHistory: Destination(route = "running_history")
    }
    object Profile : BottomNavDestination(route = "profile", iconId = R.drawable.ic_profile)
}