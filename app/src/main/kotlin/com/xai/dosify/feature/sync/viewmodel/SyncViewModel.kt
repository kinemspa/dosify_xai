package com.xai.dosify.feature.sync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.xai.dosify.core.data.repository.MedicationRepository  // Add others
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val medRepo: MedicationRepository  // Inject all
    // ...
) : ViewModel() {

    fun testSync() = viewModelScope.launch {
        val userId = auth.currentUser?.uid ?: return@launch
        medRepo.syncWithFirestore(userId)
        // Call others; add integrity (e.g., count match)
    }
}