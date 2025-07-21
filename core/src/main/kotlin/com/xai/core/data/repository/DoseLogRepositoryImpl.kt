package com.xai.core.data.repository

import com.xai.core.data.dao.DoseLogDao
import com.xai.core.data.models.DoseLog
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class DoseLogRepositoryImpl @Inject constructor(
    private val dao: DoseLogDao,
    private val firestore: FirebaseFirestore
) : DoseLogRepository {
    override suspend fun insert(log: DoseLog) = dao.insert(log)

    override fun getBySchedule(scheduleId: Long): Flow<List<DoseLog>> = dao.getBySchedule(scheduleId)

    override fun getRecent(): Flow<List<DoseLog>> = dao.getRecent()

    override suspend fun syncWithFirestore(userId: String) {
        val localLogs = dao.getAll().first()  // Assume getAll() in Dao
        val remoteCollection = firestore.collection("users/$userId/logs")

        localLogs.forEach { log ->
            remoteCollection.document(log.id.toString()).set(log).await()
        }

        val remoteLogs = remoteCollection.get().await().toObjects(DoseLog::class.java)
        remoteLogs.forEach { remote ->
            if (!localLogs.any { it.id == remote.id }) {
                dao.insert(remote)
            }
        }
    }
}