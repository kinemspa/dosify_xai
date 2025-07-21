package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.DoseScheduleDao
import com.xai.dosify.core.data.models.DoseSchedule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DoseScheduleRepository @Inject constructor(
    private val dao: DoseScheduleDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun insert(schedule: DoseSchedule) = dao.insert(schedule)

    suspend fun update(schedule: DoseSchedule) = dao.update(schedule)

    suspend fun delete(schedule: DoseSchedule) = dao.delete(schedule)

    fun getById(id: Long): Flow<DoseSchedule?> = dao.getById(id)

    fun getByMedId(medId: Long): Flow<List<DoseSchedule>> = dao.getByMedId(medId)

    fun getActive(): Flow<List<DoseSchedule>> = dao.getActive()

    suspend fun syncWithFirestore(userId: String) {
        val localSchedules = dao.getAll().first()  // Assume add getAll() to Dao if needed
        val remoteCollection = firestore.collection("users/$userId/schedules")

        localSchedules.forEach { schedule ->
            remoteCollection.document(schedule.id.toString()).set(schedule).await()
        }

        val remoteSchedules = remoteCollection.get().await().toObjects(DoseSchedule::class.java)
        remoteSchedules.forEach { remote ->
            if (!localSchedules.any { it.id == remote.id }) {
                dao.insert(remote)
            }
        }
    }
}