package com.xai.feature_sync.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.feature_sync.viewmodel.SyncViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SyncViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column (
        modifier = Modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Button(onClick = { viewModel.testSync() }) {
            Text("Test DB Integrity & Sync")
        }
        Button(onClick = {
            coroutineScope.launch {
                viewModel.manualSync(snackbarHostState)
            }
        }) {
            Text("Manual Sync")
        }
    }
    SnackbarHost( hostState = snackbarHostState, modifier = Modifier )
}