package com.xai.feature_schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
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
import com.xai.core.data.models.DoseLog
import com.xai.core.data.models.DoseSchedule
import com.xai.core.utils.setDoseAlarm
import com.xai.feature_schedule.viewmodel.DoseConfirmViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoseConfirmScreen(viewModel: DoseConfirmViewModel = hiltViewModel()) {
    val schedules by viewModel.activeSchedules.collectAsState(emptyList())
    var selectedSchedule by remember { mutableStateOf<DoseSchedule?>(null) }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.padding(16.dp)
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Schedule dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedSchedule?.let { "Med ${it.medId}: ${it.doseAmount} ${it.unit}" } ?: "Select Schedule",
                    onValueChange = { },
                    label = { Text("Schedule") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    schedules.forEach { schedule ->
                        DropdownMenuItem(
                            text = { Text("Med ${schedule.medId}: ${schedule.doseAmount} ${schedule.unit}") },
                            onClick = {
                                selectedSchedule = schedule
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Notes field
            TextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") }
            )

            Button(onClick = {
                if (selectedSchedule == null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Select a schedule")
                    }
                    return@Button
                }
                coroutineScope.launch {
                    try {
                        val doseLog = DoseLog(
                            scheduleId = selectedSchedule!!.id,
                            amountTaken = selectedSchedule!!.doseAmount,
                            takenTime = LocalDateTime.now(),
                            notes = notes.takeIf { it.isNotBlank() }
                        )
                        viewModel.doseLogRepo.insert(doseLog)
                        // Decrement stock
                        val success = viewModel.medRepo.decrementStock(selectedSchedule!!.medId, selectedSchedule!!.doseAmount)
                        if (!success) {
                            snackbarHostState.showSnackbar("Low stock")
                        } else {
                            // Set next alarm
                            setDoseAlarm(context, selectedSchedule!!)
                            snackbarHostState.showSnackbar("Dose confirmed")
                        }
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Confirm failed: ${e.message}")
                    }
                }
            }) {
                Text("Confirm Dose")
            }
        }
    }
}