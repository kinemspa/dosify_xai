package com.xai.core.data.repository

import com.xai.core.data.models.DoseLog
import kotlinx.coroutines.flow.Flow

interface DoseLogRepository {
    suspend fun insert(log: DoseLog)
    fun getBySchedule(scheduleId: Long): Flow<List<DoseLog>>
    fun getRecent(): Flow<List<DoseLog>>
    suspend fun syncWithFirestore(userId: String)
}