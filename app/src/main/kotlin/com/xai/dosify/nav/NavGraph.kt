package com.xai.dosify.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xai.dosify.feature.advanced.ui.MedFormScreen  // Add this import
import com.xai.dosify.feature.advanced.ui.ReportsScreen
import com.xai.dosify.feature.auth.LoginScreen
import com.xai.dosify.feature.sync.ui.SettingsScreen
import com.xai.dosify.ui.HomeScreen

object NavRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val MED_FORM = "med_form"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.LOGIN) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(onLoginSuccess = { navController.navigate(NavRoutes.HOME) })
        }
        composable(NavRoutes.HOME) {
            HomeScreen(navController)
        }
        composable(NavRoutes.MED_FORM) {
            MedFormScreen()
        }
        composable(NavRoutes.REPORTS) {
            ReportsScreen()
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen()
        }
    }
}