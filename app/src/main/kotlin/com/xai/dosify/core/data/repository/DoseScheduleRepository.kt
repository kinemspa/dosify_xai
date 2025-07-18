package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.DoseScheduleDao
import com.xai.dosify.core.data.models.DoseSchedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DoseScheduleRepository @Inject constructor(
    private val dao: DoseScheduleDao
) {
    suspend fun insert(schedule: DoseSchedule) = dao.insert(schedule)

    suspend fun update(schedule: DoseSchedule) = dao.update(schedule)

    suspend fun delete(schedule: DoseSchedule) = dao.delete(schedule)

    fun getById(id: Long): Flow<DoseSchedule?> = dao.getById(id)

    fun getByMedId(medId: Long): Flow<List<DoseSchedule>> = dao.getByMedId(medId)

    fun getActive(): Flow<List<DoseSchedule>> = dao.getActive()
}