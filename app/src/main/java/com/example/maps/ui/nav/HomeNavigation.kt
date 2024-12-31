package com.example.maps.ui.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.maps.ui.screens.home.HomeScreen
import com.example.maps.ui.screens.runninghistory.RunningHistoryScreen


fun NavGraphBuilder.homeNavigation (
    navController: NavController
) {

    navigation(
        startDestination = BottomNavDestination.Home.RecentRun.route,
        route = BottomNavDestination.Home.route
    ){
        composable(
            route = BottomNavDestination.Home.RecentRun.route
        ){
            HomeScreen(
                navController = navController
            )
        }
        composable(
            route = BottomNavDestination.Home.RunningHistory.route
        ){
            RunningHistoryScreen(
                navController = navController
            )

        }
    }
}