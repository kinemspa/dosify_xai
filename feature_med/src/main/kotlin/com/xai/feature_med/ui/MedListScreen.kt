package com.xai.feature_med.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.feature_med.viewmodel.MedListViewModel

@Composable
fun MedListScreen(
    viewModel: MedListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier // Add modifier parameter to accept innerPadding
) {
    val meds by viewModel.meds.collectAsState(emptyList())

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        items(meds) { med ->
            Text("${med.name} - Stock: ${med.stock}")
        }
    }
}