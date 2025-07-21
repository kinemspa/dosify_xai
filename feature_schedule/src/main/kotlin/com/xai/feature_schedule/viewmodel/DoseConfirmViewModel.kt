package com.xai.dosify.feature.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.dosify.core.data.models.DoseSchedule
import com.xai.dosify.core.data.repository.DoseLogRepository
import com.xai.dosify.core.data.repository.DoseScheduleRepository
import com.xai.dosify.core.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DoseConfirmViewModel @Inject constructor(
    val doseLogRepo: DoseLogRepository,
    val medRepo: MedicationRepository,
    private val scheduleRepo: DoseScheduleRepository
) : ViewModel() {
    val activeSchedules: Flow<List<DoseSchedule>> = scheduleRepo.getActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}