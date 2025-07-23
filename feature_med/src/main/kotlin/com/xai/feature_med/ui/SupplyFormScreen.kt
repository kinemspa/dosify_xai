package com.xai.feature_med.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.core.data.models.Supply
import com.xai.feature_med.viewmodel.SupplyViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyFormScreen(innerPadding: PaddingValues) { // Add innerPadding parameter
    val viewModel: SupplyViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding) // Apply Scaffold padding
                .padding(innerPadding) // Apply innerPadding from nav graph
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = lowStockThreshold,
                onValueChange = { lowStockThreshold = it },
                label = { Text("Low Stock Threshold") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    Timber.d("Save supply button clicked")
                    val supply = Supply(
                        name = name,
                        unit = unit,
                        stock = stock.toDoubleOrNull() ?: 0.0,
                        lowStockThreshold = lowStockThreshold.toDoubleOrNull() ?: 0.0
                    )
                    coroutineScope.launch {
                        try {
                            viewModel.insert(supply)
                            snackbarHostState.showSnackbar("Supply saved")
                            Timber.d("Save success")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Save failed: ${e.message}")
                            Timber.e(e, "Save error")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}