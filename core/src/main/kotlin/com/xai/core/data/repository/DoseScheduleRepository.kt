package com.xai.core.data.repository

import com.xai.core.data.models.DoseSchedule
import kotlinx.coroutines.flow.Flow

interface DoseScheduleRepository {
    suspend fun insert(schedule: DoseSchedule)
    suspend fun update(schedule: DoseSchedule)
    suspend fun delete(schedule: DoseSchedule)
    fun getById(id: Long): Flow<DoseSchedule?>
    fun getByMedId(medId: Long): Flow<List<DoseSchedule>>
    fun getActive(): Flow<List<DoseSchedule>>
    suspend fun syncWithFirestore(userId: String)
    fun getAll(): Flow<List<DoseSchedule>>
}