package com.xai.dosify.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xai.feature.advanced.ui.ReportsScreen
import com.xai.feature_auth.LoginScreen
import com.xai.feature_med.ui.MedFormScreen
import com.xai.feature_med.ui.MedListScreen
import com.xai.feature_schedule.ui.DoseConfirmScreen
import com.xai.feature_schedule.ui.ScheduleFormScreen
import com.xai.feature_sync.ui.SettingsScreen
import com.xai.dosify.ui.HomeScreen
import com.xai.feature_schedule.ui.CalendarScreen

object NavRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val MED_FORM = "med_form"
    const val MED_LIST = "med_list"
    const val SCHEDULE_FORM = "schedule_form"
    const val DOSE_CONFIRM = "dose_confirm"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"

    const val CALENDAR = "calendar"
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
        composable(NavRoutes.MED_LIST) {
            MedListScreen()
        }
        composable(NavRoutes.SCHEDULE_FORM) {
            ScheduleFormScreen()
        }
        composable(NavRoutes.DOSE_CONFIRM) {
            DoseConfirmScreen()
        }
        composable(NavRoutes.REPORTS) {
            ReportsScreen()
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen()
        }
        composable(NavRoutes.CALENDAR) {
            CalendarScreen()
        }
    }
}