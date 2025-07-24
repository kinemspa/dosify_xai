package com.xai.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.xai.core.nav.NavRoutes
import com.xai.core.ui.theme.PrimaryBlue
import com.xai.core.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosifyAppBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true
) {
    TopAppBar(
        title = { Text(text = title, color = TextBlack) },
        navigationIcon = {
            if (showBackButton && navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(NavRoutes.HOME) { popUpTo(NavRoutes.HOME) { inclusive = true } } }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryBlue,
            titleContentColor = TextBlack,
            navigationIconContentColor = TextBlack,
            actionIconContentColor = TextBlack
        )
    )
}