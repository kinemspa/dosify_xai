package com.xai.dosify.core.com.xai.core.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xai.dosify.core.com.xai.core.ui.components.DosifyAppBar
import com.xai.dosify.core.com.xai.core.ui.theme.DosifyTheme
import com.xai.feature.med.ui.MedFormScreen
import com.xai.feature.schedule.ui.DoseConfirmScreen
import com.xai.dosify.ui.HomeScreen
import com.xai.dosify.nav.NavRoutes

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DosifyTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        DosifyAppBar(
                            title = when (navController.currentDestination?.route) {
                                NavRoutes.HOME -> "Dosify Home"
                                NavRoutes.MED_FORM -> "Add Medication"
                                NavRoutes.DOSE_CONFIRM -> "Confirm Dose"
                                else -> "Dosify"
                            },
                            navController = navController,
                            showBackButton = navController.currentDestination?.route != NavRoutes.HOME
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.HOME,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavRoutes.HOME) { HomeScreen(navController) }
                        composable(NavRoutes.MED_FORM) { MedFormScreen() }
                        composable(NavRoutes.DOSE_CONFIRM) { DoseConfirmScreen() }
                    }
                }
            }
        }
    }
}