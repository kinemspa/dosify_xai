package com.xai.dosify.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xai.core.nav.NavRoutes
import com.xai.dosify.ui.HomeScreen
import com.xai.feature_advanced.ui.ReportsScreen
import com.xai.feature_auth.LoginScreen
import com.xai.feature_med.ui.MedFormScreen
import com.xai.feature_med.ui.MedListScreen
import com.xai.feature_med.ui.SupplyFormScreen
import com.xai.feature_schedule.ui.DoseConfirmScreen
import com.xai.feature_schedule.ui.ScheduleFormScreen
import com.xai.feature_schedule.ui.CalendarScreen
import com.xai.feature_sync.ui.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN,
        modifier = modifier
    ) {
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
            MedListScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
        composable(NavRoutes.SCHEDULE_FORM) {
            ScheduleFormScreen()
        }
        composable(NavRoutes.DOSE_CONFIRM) {
            DoseConfirmScreen()
        }
        composable(NavRoutes.REPORTS) {
            ReportsScreen(hiltViewModel())
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
        composable(NavRoutes.CALENDAR) {
            CalendarScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
        composable(NavRoutes.SUPPLY_FORM) {
            SupplyFormScreen()
        }
    }
}