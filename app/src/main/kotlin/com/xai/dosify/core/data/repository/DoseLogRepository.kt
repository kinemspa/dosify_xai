package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.DoseLogDao
import com.xai.dosify.core.data.models.DoseLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DoseLogRepository @Inject constructor(
    private val dao: DoseLogDao
) {
    suspend fun insert(log: DoseLog) = dao.insert(log)

    fun getBySchedule(scheduleId: Long): Flow<List<DoseLog>> = dao.getBySchedule(scheduleId)

    fun getRecent(): Flow<List<DoseLog>> = dao.getRecent()
}