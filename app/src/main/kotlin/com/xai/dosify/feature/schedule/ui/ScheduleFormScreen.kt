package com.xai.dosify.feature.schedule.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.dosify.core.data.models.DoseSchedule
import com.xai.dosify.feature.iap.viewmodel.IapViewModel
import com.xai.dosify.feature.schedule.viewmodel.ScheduleViewModel

@Composable
fun ScheduleFormScreen(viewModel: ScheduleViewModel = hiltViewModel(), iapViewModel: IapViewModel = hiltViewModel()) {
    val isPremium by iapViewModel.isPremium.collectAsState(false)
    var doseAmount by remember { mutableStateOf("") }
    // Other fields: medId, unit, frequency, times, startDate, etc.
    var cycleWeeks by remember { mutableStateOf("") }
    // Titration steps: List add UI stub

    Column {
        TextField(value = doseAmount, onValueChange = { doseAmount = it }, label = { Text("Dose Amount") })
        // Basic fields...

        if (isPremium) {
            TextField(value = cycleWeeks, onValueChange = { cycleWeeks = it }, label = { Text("Cycle Weeks") })
            // Cycle off, isCycling checkbox, titration list add
        } else {
            Text("Premium feature - Subscribe for cycling/titration")
            Button(onClick = { /* Launch billing flow in VM */ }) {
                Text("Subscribe")
            }
        }

        Button(onClick = {
            val schedule = DoseSchedule(
                // Fill from fields
                doseAmount = doseAmount.toDoubleOrNull() ?: 0.0
                // ...
            )
            viewModel.save(schedule)
        }) {
            Text("Save Schedule")
        }
    }
}