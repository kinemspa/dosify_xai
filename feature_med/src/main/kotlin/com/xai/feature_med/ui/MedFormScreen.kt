package com.xai.feature_med.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.core.data.models.MedType
import com.xai.core.data.models.Medication
import com.xai.feature.advanced.viewmodel.MedViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedFormScreen(viewModel: MedViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var strength by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("") }
    var powder by remember { mutableStateOf("") }
    var solvent by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MedType.TABLET) }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            TextField(value = strength, onValueChange = { strength = it }, label = { Text("Strength") })
            TextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") })
            TextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") })
            TextField(value = lowStockThreshold, onValueChange = { lowStockThreshold = it }, label = { Text("Low Stock Threshold") })

            // Type dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = type.name,
                    onValueChange = { },
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    MedType.values().forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.name) },
                            onClick = {
                                type = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            if (type == MedType.INJECTION) {
                TextField(value = powder, onValueChange = { powder = it }, label = { Text("Powder Amount") })
                TextField(value = solvent, onValueChange = { solvent = it }, label = { Text("Solvent Volume") })
            }
            Button(onClick = {
                Timber.d("Save button clicked")
                val med = Medication(
                    name = name,
                    type = type,
                    strength = strength.toDoubleOrNull() ?: 0.0,
                    unit = unit,
                    stock = stock.toIntOrNull() ?: 0,
                    lowStockThreshold = lowStockThreshold.toIntOrNull() ?: 0,
                    reconstitution = type == MedType.INJECTION
                )
                coroutineScope.launch {
                    try {
                        if (med.reconstitution) {
                            viewModel.saveWithReconstitution(med, powder.toDoubleOrNull() ?: 0.0, solvent.toDoubleOrNull() ?: 0.0)
                        } else {
                            viewModel.insert(med)
                        }
                        snackbarHostState.showSnackbar("Medication saved")
                        Timber.d("Save success")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Save failed: ${e.message}")
                        Timber.e(e, "Save error")
                    }
                }
            }) {
                Text("Save")
            }
        }
    }
}