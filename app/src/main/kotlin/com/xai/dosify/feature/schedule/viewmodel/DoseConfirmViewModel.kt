package com.xai.dosify.feature.schedule.viewmodel

import androidx.lifecycle.ViewModel
import com.xai.dosify.core.data.repository.DoseLogRepository
import com.xai.dosify.core.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoseConfirmViewModel @Inject constructor(
    val doseLogRepo: DoseLogRepository,
    val medRepo: MedicationRepository
) : ViewModel()