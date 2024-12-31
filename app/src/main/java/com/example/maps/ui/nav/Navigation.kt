package com.example.maps.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.maps.ui.screens.currentRun.CurrentRunScreen
import com.example.maps.ui.screens.profile.ProfileScreen
import com.example.maps.ui.screens.runstats.RunStatsScreen
import com.sdevprem.runtrack.ui.screen.onboard.OnBoardScreen


@Composable
fun Navigation(
    navController: NavHostController
) {
    SetNavGraph(navController)
}

@Composable
fun SetNavGraph(
    navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Home.route
    ) {
        // Nested Navigation
        homeNavigation(navController)

        composable(BottomNavDestination.Profile.route) {
            ProfileScreen()
        }
        composable(
            route = Destination.CurrentRun.route,
            deepLinks = Destination.CurrentRun.deepLinks
        ) {
            CurrentRunScreen(navController)
        }

        composable(
            route = Destination.OnBoardingDestination.route
        ) {
            OnBoardScreen(navController = navController)
        }

        composable(route = Destination.RunStats.route) {
            RunStatsScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
    }
}