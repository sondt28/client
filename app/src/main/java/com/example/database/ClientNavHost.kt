package com.example.database

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.database.ui.getallstudent.GetAllScreen
import com.example.database.ui.home.HomeScreen
import com.example.database.ui.search.SearchScreen
import com.example.database.ui.top10bysum.Top10BySumScreen
import com.example.database.ui.top10studentsubject.Top10BySubjectScreen

@Composable
fun ClientNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    dbService: IStudentAPI
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
        modifier = modifier
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen {
                navController.navigationSingleTopTo(it.route)
            }
        }
        composable(route = GetAllScreen.route) {
            GetAllScreen(dbService = dbService, onBackPressed = { navController.popBackStack() })
        }
        composable(route = Top10BySubjectScreen.route) {
            Top10BySubjectScreen(dbService = dbService, onBackPressed = { navController.popBackStack() })
        }
        composable(route = Top10BySumScreen.route) {
            Top10BySumScreen(dbService = dbService, onBackPressed = { navController.popBackStack() })
        }
        composable(route = SearchScreen.route) {
            SearchScreen(dbService = dbService, onBackPressed = { navController.popBackStack() })
        }
    }
}

fun NavHostController.navigationSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigationSingleTopTo.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }