package com.xai.feature_sync.viewmodel

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.xai.core.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import com.xai.core.data.AppDatabase
import android.content.Context
import javax.inject.Inject
import com.xai.dosify.core.utils.backupDatabase
import com.xai.dosify.core.utils.restoreDatabase

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val medRepo: MedicationRepository,
    private val scheduleRepo: DoseScheduleRepository,
    private val logRepo: DoseLogRepository,
    private val supplyRepo: SupplyRepository,
    private val reconstRepo: ReconstitutionRepository,
    private val profileRepo: ProfileRepository,
    private val db: AppDatabase // Injected for backup/restore
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
            profileRepo.syncWithFirestore(userId)
            snackbarHostState.showSnackbar("Synced successfully")
            Timber.d("Manual sync success")
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Sync failed: ${e.message}")
            Timber.e(e, "Manual sync error")
        }
    }

    fun backupData(context: Context) = viewModelScope.launch {
        backupDatabase(db, context)
        Timber.d("Backup initiated")
    }

    fun restoreData(context: Context, backupFilePath: String) = viewModelScope.launch {
        restoreDatabase(db, context, backupFilePath)
        Timber.d("Restore initiated")
    }
}