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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import timber.log.Timber
import android.Manifest

@Composable
fun SettingsScreen(
    viewModel: SyncViewModel = hiltViewModel(),
    modifier: Modifier = Modifier // Add modifier parameter to accept innerPadding
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            viewModel.backupData(context) // Pass context
            Timber.d("Backup permission granted")
        } else {
            Timber.w("Backup storage permission denied")
        }
    }

    val restorePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val path = uri.path ?: return@rememberLauncherForActivityResult
            viewModel.restoreData(context, path) // Pass context and path
            Timber.d("Restore file selected: $path")
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            restorePicker.launch("*/*") // Launch file picker
            Timber.d("Restore permission granted")
        } else {
            Timber.w("Restore storage permission denied")
        }
    }

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
        Button(
            onClick = { backupLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Backup Data")
        }
        Button(
            onClick = { restoreLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restore Data")
        }
    }
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier)
}