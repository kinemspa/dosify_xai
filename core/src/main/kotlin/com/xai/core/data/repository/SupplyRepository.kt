package com.xai.core.data.repository

import com.xai.core.data.models.Supply
import kotlinx.coroutines.flow.Flow

interface SupplyRepository {
    suspend fun insert(supply: Supply)
    suspend fun update(supply: Supply)
    suspend fun delete(supply: Supply)
    fun getById(id: Long): Flow<Supply?>
    fun getAll(): Flow<List<Supply>>
    suspend fun decrementStock(supplyId: Long, amount: Double): Boolean
    suspend fun syncWithFirestore(userId: String)
}