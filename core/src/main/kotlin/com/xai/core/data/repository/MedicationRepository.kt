package com.xai.core.data.repository

import com.xai.core.data.models.Medication
import com.xai.core.data.models.Reconstitution
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    suspend fun insert(medication: Medication)
    suspend fun update(medication: Medication)
    suspend fun delete(medication: Medication)
    fun getById(id: Long): Flow<Medication?>
    fun getAll(): Flow<List<Medication>>
    suspend fun decrementStock(medId: Long, amount: Double): Boolean
    suspend fun syncWithFirestore(userId: String)
    suspend fun saveWithReconstitution(med: Medication, powder: Double, solvent: Double)
}