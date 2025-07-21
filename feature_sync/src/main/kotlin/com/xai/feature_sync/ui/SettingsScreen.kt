package com.xai.feature_sync.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.feature_sync.viewmodel.SyncViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SyncViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column {
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
    SnackbarHost(snackbarHostState)
}