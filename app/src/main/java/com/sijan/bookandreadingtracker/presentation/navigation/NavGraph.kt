package com.sijan.bookandreadingtracker.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sijan.bookandreadingtracker.presentation.screens.BookDetailsScreen
import com.sijan.bookandreadingtracker.presentation.screens.DashboardScreen
import com.sijan.bookandreadingtracker.presentation.screens.LibraryScreen
import com.sijan.bookandreadingtracker.presentation.screens.ProfileScreen
import com.sijan.bookandreadingtracker.presentation.screens.ReaderScreen
import com.sijan.bookandreadingtracker.presentation.screens.RecommendationScreen

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BookTrackerApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Home, "Dashboard"),
        BottomNavItem(Screen.Library, Icons.Default.List, "Library"),
        BottomNavItem(Screen.Recommendation, Icons.Default.Search, "Discover"),
        BottomNavItem(Screen.Profile, Icons.Default.Person, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on main screens
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Library.route,
        Screen.Recommendation.route,
        Screen.Profile.route
    )

    if (showBottomBar) {
        NavigationBar {
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentDestination?.hierarchy?.any {
                        it.route == item.screen.route
                    } == true,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                },
                onAddBooksClick = {
                    navController.navigate(Screen.Recommendation.route)
                }
            )
        }

        composable(Screen.Recommendation.route) {
            RecommendationScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.BookDetails.route) {
            BookDetailsScreen(
                onNavigateBack = { navController.navigateUp() },
                onStartReading = { bookId ->
                    navController.navigate(Screen.Reader.createRoute(bookId))
                }
            )
        }

        composable(Screen.Reader.route) {
            ReaderScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

