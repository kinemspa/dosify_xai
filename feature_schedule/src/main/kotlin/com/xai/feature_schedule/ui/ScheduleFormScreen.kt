package com.xai.feature_schedule.ui

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.xai.core.data.models.DoseSchedule
import com.xai.core.data.models.Frequency
import com.xai.core.data.models.Medication
import com.xai.core.utils.setDoseAlarm
import com.xai.feature_iap.viewmodel.IapViewModel
import com.xai.feature_med.viewmodel.MedListViewModel
import com.xai.feature_schedule.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
// Removed @RequiresApi(Build.VERSION_CODES.O) to support API 24+
@Composable
fun ScheduleFormScreen(innerPadding: PaddingValues) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val iapViewModel: IapViewModel = hiltViewModel()
    val medViewModel: MedListViewModel = hiltViewModel()
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            TextField(
                value = doseAmount,
                onValueChange = { doseAmount = it },
                label = { Text("Dose Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedFreq,
                    onDismissRequest = { expandedFreq = false }
                ) {
                    Frequency.entries.forEach { freq ->
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

            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            times = times + LocalTime.of(hour, minute) // Should work with desugaring
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Dose Time")
            }
            Text("Times: ${times.joinToString { time ->
                if (Build.VERSION.SDK_INT >= 26) {
                    time.format(DateTimeFormatter.ofPattern("HH:mm")) // Use with desugaring
                } else {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, time.hour)
                        set(Calendar.MINUTE, time.minute)
                    }
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
                }
            }}")

            if (isPremium) {
                TextField(
                    value = cycleWeeks,
                    onValueChange = { cycleWeeks = it },
                    label = { Text("Cycle Weeks") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Premium feature - Subscribe for cycling/titration")
                Button(
                    onClick = { /* Launch billing flow later */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Subscribe")
                }
            }

            Button(
                onClick = {
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
                            setDoseAlarm(context, schedule)
                            snackbarHostState.showSnackbar("Schedule saved")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Save failed: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Schedule")
            }
        }
    }
}