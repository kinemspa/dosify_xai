package com.xai.dosify.feature.sync.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.xai.dosify.core.data.repository.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val medRepo: MedicationRepository,
    private val scheduleRepo: DoseScheduleRepository,
    private val logRepo: DoseLogRepository,
    private val supplyRepo: SupplyRepository,
    private val reconstRepo: ReconstitutionRepository,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = auth.currentUser?.uid ?: return Result.failure()
        medRepo.syncWithFirestore(userId)
        scheduleRepo.syncWithFirestore(userId)
        logRepo.syncWithFirestore(userId)
        supplyRepo.syncWithFirestore(userId)
        reconstRepo.syncWithFirestore(userId)
        return Result.success()
    }
}