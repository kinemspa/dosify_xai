package com.xai.dosify.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.xai.dosify.nav.NavRoutes

@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Text("Home - Dosify Dashboard")
        Button(onClick = { navController.navigate(NavRoutes.MED_FORM) }) {
            Text("Add Medication")
        }
        Button(onClick = { navController.navigate(NavRoutes.REPORTS) }) {
            Text("View Reports")
        }
        Button(onClick = { navController.navigate(NavRoutes.SETTINGS) }) {
            Text("Settings & Diagnostics")
        }
    }
}