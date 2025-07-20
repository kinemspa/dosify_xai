package com.xai.dosify.feature.advanced.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.dosify.core.data.models.MedType
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.feature.med.viewmodel.MedViewModel

@OptIn(ExperimentalMaterial3Api::class)  // Add for experimental APIs
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

    Column(
        modifier = Modifier.padding(16.dp),
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
            val med = Medication(
                name = name,
                type = type,
                strength = strength.toDoubleOrNull() ?: 0.0,
                unit = unit,
                stock = stock.toDoubleOrNull() ?: 0.0,
                lowStockThreshold = lowStockThreshold.toDoubleOrNull() ?: 0.0,
                reconstitution = type == MedType.INJECTION
            )
            if (med.reconstitution) {
                viewModel.saveWithReconstitution(med, powder.toDoubleOrNull() ?: 0.0, solvent.toDoubleOrNull() ?: 0.0)  // Matches VM
            } else {
                viewModel.insert(med)
            }
        }) {
            Text("Save")
        }
    }
}