package com.xai.feature.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.dosify.core.data.models.DoseSchedule
import com.xai.dosify.core.data.repository.DoseScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repo: DoseScheduleRepository
) : ViewModel() {
    val schedules = repo.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}