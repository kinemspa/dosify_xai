package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.SupplyDao
import com.xai.dosify.core.data.models.Supply
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SupplyRepository @Inject constructor(
    private val dao: SupplyDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun insert(supply: Supply) = dao.insert(supply)

    suspend fun update(supply: Supply) = dao.update(supply)

    suspend fun delete(supply: Supply) = dao.delete(supply)

    fun getById(id: Long): Flow<Supply?> = dao.getById(id)

    fun getAll(): Flow<List<Supply>> = dao.getAll()

    suspend fun decrementStock(supplyId: Long, amount: Double): Boolean = dao.decrementStock(supplyId, amount) > 0

    suspend fun syncWithFirestore(userId: String) {
        val localSupplies = dao.getAll().first()
        val remoteCollection = firestore.collection("users/$userId/supplies")

        localSupplies.forEach { supply ->
            remoteCollection.document(supply.id.toString()).set(supply).await()
        }

        val remoteSupplies = remoteCollection.get().await().toObjects(Supply::class.java)
        remoteSupplies.forEach { remote ->
            if (!localSupplies.any { it.id == remote.id }) {
                dao.insert(remote)
            }
        }
    }
}