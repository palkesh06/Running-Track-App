package com.example.maps.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.maps.R
import com.example.maps.ui.common.compose.animation.ComposeUtil
import com.example.maps.ui.common.compose.compositionLocal.LocalScaffoldBottomPadding
import com.example.maps.ui.common.extensions.navigateToBottomNavDestination
import com.example.maps.ui.nav.BottomNavDestination
import com.example.maps.ui.nav.Destination
import com.example.maps.ui.nav.Navigation
import com.example.maps.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlin.collections.listOf


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(rememberNavController())
        }
    }
}

@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    var shouldShowBottomNav by rememberSaveable { mutableStateOf(false) }
    var shouldShowFAB by rememberSaveable { mutableStateOf(false) }
    var hideBottomItems by rememberSaveable { mutableStateOf(true) }

    hideBottomItems = when (navBackStackEntry?.destination?.route) {
        Destination.CurrentRun.route -> true
        Destination.OnBoardingDestination.route -> true
        Destination.RunStats.route -> true
        else -> false
    }

    LaunchedEffect(hideBottomItems) {
        if (hideBottomItems) {
            shouldShowFAB = false
            delay(150)
            shouldShowBottomNav = false
        } else {
            shouldShowBottomNav = true
            delay(150)
            shouldShowFAB = true
        }
    }
    Scaffold(
        bottomBar = {
            ComposeUtil.SlideUpAnimationVertically(
                visible = shouldShowBottomNav
            ) {
                BottomBar(navHostController)
            }
        },
        floatingActionButton = {
            ShrinkAnimatedVisibility(visible = shouldShowFAB) {
                FloatingActionButton(
                    onClick = {
                        if (navHostController.currentDestination?.route != Destination.CurrentRun.route) {
                            navHostController.navigate(
                                Destination.CurrentRun.route
                            )
                        }
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_run),
                        contentDescription = "Run Icon",
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(
                LocalScaffoldBottomPadding provides paddingValues.calculateBottomPadding(),
            ) {
                Navigation(navHostController)
            }
        }
    }
}

@Composable
fun ShrinkAnimatedVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(150)
        ),
        exit = scaleOut(
            animationSpec = tween(150)
        ),
        content = content
    )
}

@Composable
fun BottomBar(
    navHostController: NavHostController
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary,
                clip = true,
            )
    ) {
        BottomNavBar(
            navHostController,
            items = listOf(
                BottomNavDestination.Home,
                BottomNavDestination.Profile
            ),
            modifier = Modifier
        )
    }
}


@Composable
fun BottomNavBar(
    navHostController: NavHostController,
    items: List<BottomNavDestination>,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 0.dp
    ) {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach {
            BottomNavItem(it, navHostController, currentDestination)
        }

    }
}


@Composable
private fun RowScope.BottomNavItem(
    item: BottomNavDestination,
    navController: NavController,
    currentDestination: NavDestination?
) {
    BottomNavigationItem(
        icon = {
            Icon(
                imageVector = item.getIconVector(),
                contentDescription = ""
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == item.route
        } == true,
        onClick = {
            if (item.route != currentDestination?.route) {
                navController.navigateToBottomNavDestination(item)
            }
        },
        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        selectedContentColor = MaterialTheme.colorScheme.primary,
    )
}
