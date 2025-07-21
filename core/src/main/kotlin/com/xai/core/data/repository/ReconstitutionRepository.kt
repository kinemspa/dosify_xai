package com.xai.core.data.repository

import com.xai.core.data.models.Reconstitution
import kotlinx.coroutines.flow.Flow

interface ReconstitutionRepository {
    suspend fun insert(reconst: Reconstitution)
    suspend fun update(reconst: Reconstitution)
    fun getByMedId(medId: Long): Flow<Reconstitution?>
    suspend fun syncWithFirestore(userId: String)
}