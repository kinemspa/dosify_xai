package com.xai.feature.med.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.feature.med.viewmodel.MedListViewModel

@Composable
fun MedListScreen(viewModel: MedListViewModel = hiltViewModel()) {
    val meds by viewModel.meds.collectAsState(emptyList())

    LazyColumn {
        items(meds) { med ->
            Text("${med.name} - Stock: ${med.stock}")
        }
    }
}