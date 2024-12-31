package com.example.maps.ui.nav

import androidx.navigation.NavController
import androidx.navigation.navDeepLink


sealed class Destination(val route: String) {
    object OnBoardingDestination : Destination("on_boarding") {
        fun navigateToHome(navController: NavController) {
            navController.navigate(BottomNavDestination.Home.route) {
                popUpTo(route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    object CurrentRun : Destination("current_run"){
        val currentRunUriPattern = "https://fitpath.domain.com/$route"
        val deepLinks = listOf(
            navDeepLink {
                uriPattern = currentRunUriPattern
            }
        )
    }

    data object RunStats : Destination("run_stats")

    //global navigation
    companion object {
        fun navigateToCurrentRunScreen(navController: NavController) {
            navController.navigate(CurrentRun.route)
        }
    }
}