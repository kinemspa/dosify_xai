package com.xai.dosify.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xai.core.nav.NavRoutes
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.feature_auth.AuthViewModel

@Composable
fun HomeScreen(navController: NavController, innerPadding: PaddingValues) {
    val authViewModel: AuthViewModel = hiltViewModel() // Inject for logout

    Column(
        modifier = Modifier
            .padding(innerPadding) // Apply innerPadding to shift content below app bar
            .padding(horizontal = 16.dp)
    ) {
        Text("Home - Dosify Dashboard")
        Button(onClick = { navController.navigate(NavRoutes.MED_FORM) }) {
            Text("Add Medication")
        }
        Button(onClick = { navController.navigate(NavRoutes.MED_LIST) }) {
            Text("View Medications")
        }
        Button(onClick = { navController.navigate(NavRoutes.SCHEDULE_FORM) }) {
            Text("Add Schedule")
        }
        Button(onClick = { navController.navigate(NavRoutes.CALENDAR) }) {
            Text("View Calendar")
        }
        Button(onClick = { navController.navigate(NavRoutes.DOSE_CONFIRM) }) {
            Text("Confirm Dose")
        }
        Button(onClick = { navController.navigate(NavRoutes.REPORTS) }) {
            Text("View Reports")
        }
        Button(onClick = { navController.navigate(NavRoutes.SETTINGS) }) {
            Text("Settings & Diagnostics")
        }
        Button(onClick = { navController.navigate(NavRoutes.SUPPLY_FORM) }) {
            Text("Add Supply")
        }
        Button(onClick = {
            authViewModel.logout()
            navController.navigate(NavRoutes.LOGIN) { popUpTo(0) { inclusive = true } } // Clear back stack
        }) {
            Text("Logout") // Added logout button
        }
    }
}