package com.xai.feature_sync.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.xai.core.data.repository.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.Locale

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val medRepo: MedicationRepository,
    private val scheduleRepo: DoseScheduleRepository,
    private val logRepo: DoseLogRepository,
    private val supplyRepo: SupplyRepository,
    private val reconstRepo: ReconstitutionRepository,
    private val profileRepo: ProfileRepository,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker started")
        if (Locale.getDefault().country.equals("CN", ignoreCase = true)) {
            Timber.w("Firebase disabled in China - local mode only")
            return Result.success() // Local mode, no sync
        }
        val userId = auth.currentUser?.uid ?: run {
            Timber.w("No userId - sync failed")
            return Result.failure()
        }
        try {
            medRepo.syncWithFirestore(userId)
            scheduleRepo.syncWithFirestore(userId)
            logRepo.syncWithFirestore(userId)
            supplyRepo.syncWithFirestore(userId)
            reconstRepo.syncWithFirestore(userId)
            profileRepo.syncWithFirestore(userId)
            Timber.d("Sync success")
            return Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Sync error")
            return Result.retry()
        }
    }
}