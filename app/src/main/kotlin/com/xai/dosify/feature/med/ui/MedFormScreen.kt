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
    var strength by remember { mutableStateOf(0.0) }
    // ... other fields
    var powder by remember { mutableStateOf(0.0) }
    var solvent by remember { mutableStateOf(0.0) }
    val type = MedType.INJECTION  // Assume selector

    Column {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        // Other fields...
        if (type == MedType.INJECTION) {
            TextField(value = powder.toString(), onValueChange = { powder = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Powder Amount") })
            TextField(value = solvent.toString(), onValueChange = { solvent = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Solvent Volume") })
        }
        Button(onClick = {
            val med = Medication(name = name, strength = strength, /* ... */ reconstitution = type == MedType.INJECTION)
            if (med.reconstitution) {
                viewModel.saveWithReconst(med, powder, solvent)
            } else {
                viewModel.insert(med)
            }
        }) {
            Text("Save")
        }
    }
}