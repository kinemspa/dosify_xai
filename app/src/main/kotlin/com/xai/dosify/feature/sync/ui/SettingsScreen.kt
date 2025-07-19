package com.xai.dosify.feature.sync.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.dosify.feature.sync.viewmodel.SyncViewModel

@Composable
fun SettingsScreen(viewModel: SyncViewModel = hiltViewModel()) {
    Column {
        Button(onClick = { viewModel.testSync() }) {
            Text("Test DB Integrity & Sync")
        }
    }
}