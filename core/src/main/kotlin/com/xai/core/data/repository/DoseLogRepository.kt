package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.DoseLogDao
import com.xai.dosify.core.data.models.DoseLog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DoseLogRepository @Inject constructor(
    private val dao: DoseLogDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun insert(log: DoseLog) = dao.insert(log)

    fun getBySchedule(scheduleId: Long): Flow<List<DoseLog>> = dao.getBySchedule(scheduleId)

    fun getRecent(): Flow<List<DoseLog>> = dao.getRecent()

    suspend fun syncWithFirestore(userId: String) {
        val localLogs = dao.getAll().first()  // Assume add getAll() to Dao
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