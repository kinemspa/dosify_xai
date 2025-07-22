package com.xai.core.data.repository

import com.xai.core.data.dao.DoseScheduleDao
import com.xai.core.data.models.DoseSchedule
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class DoseScheduleRepositoryImpl @Inject constructor(
    private val dao: DoseScheduleDao,
    private val firestore: FirebaseFirestore

) : DoseScheduleRepository {

    override suspend fun insert(schedule: DoseSchedule) = dao.insert(schedule)

    override suspend fun update(schedule: DoseSchedule) = dao.update(schedule)

    override suspend fun delete(schedule: DoseSchedule) = dao.delete(schedule)

    override fun getById(id: Long): Flow<DoseSchedule?> = dao.getById(id)

    override fun getByMedId(medId: Long): Flow<List<DoseSchedule>> = dao.getByMedId(medId)

    override fun getActive(): Flow<List<DoseSchedule>> = dao.getActive()

    fun getAll(): Flow<List<DoseSchedule>> = dao.getAll()

    override suspend fun syncWithFirestore(userId: String) {
        val localSchedules = dao.getAll().first()
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