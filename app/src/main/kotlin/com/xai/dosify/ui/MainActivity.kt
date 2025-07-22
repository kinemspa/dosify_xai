package com.xai.dosify.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.xai.core.ui.theme.DosifyTheme
import com.xai.core.ui.components.DosifyAppBar
import com.xai.dosify.nav.AppNavGraph
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
                                NavRoutes.LOGIN -> "Login"
                                NavRoutes.HOME -> "Dosify Home"
                                NavRoutes.MED_FORM -> "Add Medication"
                                NavRoutes.MED_LIST -> "Medication List"
                                NavRoutes.SCHEDULE_FORM -> "Add Schedule"
                                NavRoutes.DOSE_CONFIRM -> "Confirm Dose"
                                NavRoutes.REPORTS -> "Reports"
                                NavRoutes.SETTINGS -> "Settings"
                                NavRoutes.CALENDAR -> "Calendar"
                                NavRoutes.SUPPLY_FORM -> "Add Supply"
                                else -> "Dosify"
                            },
                            navController = navController,
                            showBackButton = navController.currentDestination?.route != NavRoutes.LOGIN
                        )
                    }
                ) { innerPadding ->
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}