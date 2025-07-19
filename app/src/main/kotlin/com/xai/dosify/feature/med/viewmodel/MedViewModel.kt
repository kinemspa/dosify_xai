package com.xai.dosify.feature.med.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.core.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedViewModel @Inject constructor(
    private val repo: MedicationRepository
) : ViewModel() {

    fun insert(med: Medication) = viewModelScope.launch {
        repo.insert(med)
    }

    fun saveWithReconst(med: Medication, powder: Double, solvent: Double) = viewModelScope.launch {
        repo.saveWithReconstitution(med, powder, solvent)
    }
}