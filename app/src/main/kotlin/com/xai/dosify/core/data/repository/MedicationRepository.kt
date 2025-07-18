package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.MedicationDao
import com.xai.dosify.core.data.models.Medication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicationRepository @Inject constructor(
    private val dao: MedicationDao
) {
    suspend fun insert(medication: Medication) = dao.insert(medication)

    suspend fun update(medication: Medication) = dao.update(medication)

    suspend fun delete(medication: Medication) = dao.delete(medication)

    fun getById(id: Long): Flow<Medication?> = dao.getById(id)

    fun getAll(): Flow<List<Medication>> = dao.getAll()

    suspend fun decrementStock(medId: Long, amount: Double): Boolean = dao.decrementStock(medId, amount) > 0
}