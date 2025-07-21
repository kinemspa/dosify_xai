package com.xai.dosify.feature.advanced.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.core.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MedViewModel @Inject constructor(
    private val repo: MedicationRepository
) : ViewModel() {

    fun insert(med: Medication) = viewModelScope.launch {
        Timber.d("Insert med: ${med.name}")
        repo.insert(med)
    }

    fun saveWithReconstitution(med: Medication, powder: Double, solvent: Double) = viewModelScope.launch {
        Timber.d("Save reconst med: ${med.name}")
        repo.saveWithReconstitution(med, powder, solvent)
    }
}