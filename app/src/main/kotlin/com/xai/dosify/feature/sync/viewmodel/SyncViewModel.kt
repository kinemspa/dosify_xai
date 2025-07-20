package com.xai.dosify.feature.sync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.xai.dosify.core.data.repository.MedicationRepository  // Add others
import com.xai.dosify.core.data.repository.DoseLogRepository
import com.xai.dosify.core.data.repository.DoseScheduleRepository
import com.xai.dosify.core.data.repository.ReconstitutionRepository
import com.xai.dosify.core.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import androidx.compose.material3.SnackbarHostState

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val medRepo: MedicationRepository,
    private val scheduleRepo: DoseScheduleRepository,
    private val logRepo: DoseLogRepository,
    private val supplyRepo: SupplyRepository,
    private val reconstRepo: ReconstitutionRepository
) : ViewModel() {

    fun testSync() = viewModelScope.launch {
        val userId = auth.currentUser?.uid ?: return@launch
        medRepo.syncWithFirestore(userId)
        // Call others; add integrity (e.g., count match)
    }

    suspend fun manualSync(snackbarHostState: SnackbarHostState) {
        val userId = auth.currentUser?.uid ?: run {
            snackbarHostState.showSnackbar("No user logged in")
            return
        }
        try {
            medRepo.syncWithFirestore(userId)
            scheduleRepo.syncWithFirestore(userId)
            logRepo.syncWithFirestore(userId)
            supplyRepo.syncWithFirestore(userId)
            reconstRepo.syncWithFirestore(userId)
            snackbarHostState.showSnackbar("Synced successfully")
            Timber.d("Manual sync success")
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Sync failed: ${e.message}")
            Timber.e(e, "Manual sync error")
        }
    }
}