package com.xai.feature_med.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.core.data.models.Supply
import com.xai.core.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplyViewModel @Inject constructor(
    private val repo: SupplyRepository
) : ViewModel() {

    fun insert(supply: Supply) = viewModelScope.launch {
        repo.insert(supply)
    }
}