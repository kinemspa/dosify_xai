package com.xai.dosify.feature.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xai.dosify.core.data.models.DoseSchedule
import com.xai.dosify.core.data.models.Frequency
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.core.utils.setDoseAlarm
import com.xai.dosify.feature.iap.viewmodel.IapViewModel
import com.xai.dosify.feature.med.viewmodel.MedListViewModel
import com.xai.dosify.feature.schedule.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleFormScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    iapViewModel: IapViewModel = hiltViewModel(),
    medViewModel: MedListViewModel = hiltViewModel()
) {
    val isPremium by iapViewModel.isPremium.collectAsState(false)
    val meds by medViewModel.meds.collectAsState(emptyList())
    var doseAmount by remember { mutableStateOf("") }
    var selectedMed by remember { mutableStateOf<Medication?>(null) }
    var unit by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(Frequency.DAILY) }
    var times by remember { mutableStateOf(emptyList<LocalTime>()) }
    var cycleWeeks by remember { mutableStateOf("") }
    var expandedMed by remember { mutableStateOf(false) }
    var expandedFreq by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.padding(16.dp)
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Medication dropdown
            ExposedDropdownMenuBox(
                expanded = expandedMed,
                onExpandedChange = { expandedMed = !expandedMed }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedMed?.name ?: "Select Medication",
                    onValueChange = { },
                    label = { Text("Medication") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMed) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedMed,
                    onDismissRequest = { expandedMed = false }
                ) {
                    meds.forEach { med ->
                        DropdownMenuItem(
                            text = { Text(med.name) },
                            onClick = {
                                selectedMed = med
                                unit = med.unit
                                expandedMed = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Dose amount
            TextField(
                value = doseAmount,
                onValueChange = { doseAmount = it },
                label = { Text("Dose Amount") }
            )

            // Unit (auto-filled from med)
            TextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                enabled = false // Read-only from med
            )

            // Frequency dropdown
            ExposedDropdownMenuBox(
                expanded = expandedFreq,
                onExpandedChange = { expandedFreq = !expandedFreq }
            ) {
                TextField(
                    readOnly = true,
                    value = frequency.name,
                    onValueChange = { },
                    label = { Text("Frequency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFreq) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFreq,
                    onDismissRequest = { expandedFreq = false }
                ) {
                    Frequency.values().forEach { freq ->
                        DropdownMenuItem(
                            text = { Text(freq.name) },
                            onClick = {
                                frequency = freq
                                expandedFreq = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Time picker button
            Button(onClick = {
                // Simple stub: Add first time (replace with actual picker later)
                times = times + LocalTime.now()
            }) {
                Text("Add Dose Time")
            }
            Text("Times: ${times.joinToString()}")

            if (isPremium) {
                TextField(
                    value = cycleWeeks,
                    onValueChange = { cycleWeeks = it },
                    label = { Text("Cycle Weeks") }
                )
                // Add cycleOffWeeks, isCycling checkbox, titration later
            } else {
                Text("Premium feature - Subscribe for cycling/titration")
                Button(onClick = { /* Launch billing flow later */ }) {
                    Text("Subscribe")
                }
            }

            Button(onClick = {
                if (selectedMed == null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Select a medication")
                    }
                    return@Button
                }
                val schedule = DoseSchedule(
                    medId = selectedMed!!.id,
                    doseAmount = doseAmount.toDoubleOrNull() ?: 0.0,
                    unit = unit,
                    frequency = frequency,
                    times = times
                )
                coroutineScope.launch {
                    try {
                        viewModel.save(schedule)
                        setDoseAlarm(context, schedule) // Set alarm
                        snackbarHostState.showSnackbar("Schedule saved")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Save failed: ${e.message}")
                    }
                }
            }) {
                Text("Save Schedule")
            }
        }
    }
}