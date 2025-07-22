package com.xai.feature_advanced.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.core.data.models.DoseLog
import com.xai.core.data.repository.DoseLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val logRepo: DoseLogRepository
) : ViewModel() {
    val doseLogs: Flow<List<DoseLog>> = logRepo.getRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}