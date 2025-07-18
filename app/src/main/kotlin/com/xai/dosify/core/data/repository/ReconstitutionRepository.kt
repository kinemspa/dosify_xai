package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.ReconstitutionDao
import com.xai.dosify.core.data.models.Reconstitution
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReconstitutionRepository @Inject constructor(
    private val dao: ReconstitutionDao
) {
    suspend fun insert(reconst: Reconstitution) = dao.insert(reconst)

    suspend fun update(reconst: Reconstitution) = dao.update(reconst)

    fun getByMedId(medId: Long): Flow<Reconstitution?> = dao.getByMedId(medId)
}