package com.xai.feature_schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.core.data.models.DoseSchedule
import com.xai.core.data.repository.DoseScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repo: DoseScheduleRepository
) : ViewModel() {

    fun save(schedule: DoseSchedule) = viewModelScope.launch {
        repo.insert(schedule)
    }
}