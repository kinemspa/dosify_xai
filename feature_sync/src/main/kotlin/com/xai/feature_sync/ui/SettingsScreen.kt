package com.xai.feature_sync.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.feature_sync.viewmodel.SyncViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission

@Composable
fun SettingsScreen(
    viewModel: SyncViewModel = hiltViewModel(),
    modifier: Modifier = Modifier // Add modifier parameter to accept innerPadding
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            onClick = { viewModel.testSync() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test DB Integrity & Sync")
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.manualSync(snackbarHostState)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Manual Sync")
        }
    }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) viewModel.backupData() else Timber.w("Storage permission denied")
    }
    Button(onClick = { launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) }) { Text("Backup Data") }
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier)
}