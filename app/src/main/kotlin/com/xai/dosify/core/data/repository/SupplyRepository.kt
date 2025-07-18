package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.SupplyDao
import com.xai.dosify.core.data.models.Supply
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SupplyRepository @Inject constructor(
    private val dao: SupplyDao
) {
    suspend fun insert(supply: Supply) = dao.insert(supply)

    suspend fun update(supply: Supply) = dao.update(supply)

    suspend fun delete(supply: Supply) = dao.delete(supply)

    fun getById(id: Long): Flow<Supply?> = dao.getById(id)

    fun getAll(): Flow<List<Supply>> = dao.getAll()

    suspend fun decrementStock(supplyId: Long, amount: Double): Boolean = dao.decrementStock(supplyId, amount) > 0
}