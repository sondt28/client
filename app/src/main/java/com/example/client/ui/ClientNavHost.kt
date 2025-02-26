package com.example.client.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.client.service.LocalService
import com.example.client.ui.getallstudent.GetAllScreen
import com.example.client.ui.home.HomeScreen
import com.example.client.ui.search.SearchScreen
import com.example.client.ui.top10bysum.Top10BySumScreen
import com.example.client.ui.top10studentsubject.Top10BySubjectScreen

@Composable
fun ClientNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    localService: LocalService
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
        modifier = modifier
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen(localService = localService) {
                navController.navigationSingleTopTo(it.route)
            }
        }
        composable(route = GetAllScreen.route) {
            GetAllScreen(localService = localService, onBackPressed = { navController.popBackStack() })
        }
        composable(route = Top10BySubjectScreen.route) {
            Top10BySubjectScreen(localService = localService, onBackPressed = { navController.popBackStack() })
        }
        composable(route = Top10BySumScreen.route) {
            Top10BySumScreen(localService = localService, onBackPressed = { navController.popBackStack() })
        }
        composable(route = SearchScreen.route) {
            SearchScreen(localService = localService, onBackPressed = { navController.popBackStack() })
        }
    }
}

fun NavHostController.navigationSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigationSingleTopTo.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }