package com.xai.dosify.feature.med.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.dosify.core.data.models.MedType
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.feature.med.viewmodel.MedViewModel

@Composable
fun MedFormScreen(viewModel: MedViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var strength by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("") }
    var powder by remember { mutableStateOf("") }
    var solvent by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MedType.TABLET) }  // Assume dropdown/enum selector

    Column {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(value = strength, onValueChange = { strength = it }, label = { Text("Strength") })
        TextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") })
        TextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") })
        TextField(value = lowStockThreshold, onValueChange = { lowStockThreshold = it }, label = { Text("Low Stock Threshold") })
        // Type selector (e.g., DropdownMenu for MedType)
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
                viewModel.saveWithReconst(med, powder.toDoubleOrNull() ?: 0.0, solvent.toDoubleOrNull() ?: 0.0)
            } else {
                viewModel.insert(med)
            }
        }) {
            Text("Save")
        }
    }
}