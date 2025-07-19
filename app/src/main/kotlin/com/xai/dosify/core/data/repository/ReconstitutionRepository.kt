package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.ReconstitutionDao
import com.xai.dosify.core.data.models.Reconstitution
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReconstitutionRepository @Inject constructor(
    private val dao: ReconstitutionDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun insert(reconst: Reconstitution) = dao.insert(reconst)

    suspend fun update(reconst: Reconstitution) = dao.update(reconst)

    fun getByMedId(medId: Long): Flow<Reconstitution?> = dao.getByMedId(medId)

    suspend fun syncWithFirestore(userId: String) {
        val localReconsts = dao.getAll().first()  // Assume add getAll() to Dao
        val remoteCollection = firestore.collection("users/$userId/reconsts")

        localReconsts.forEach { reconst ->
            remoteCollection.document(reconst.medId.toString()).set(reconst).await()
        }

        val remoteReconsts = remoteCollection.get().await().toObjects(Reconstitution::class.java)
        remoteReconsts.forEach { remote ->
            if (!localReconsts.any { it.medId == remote.medId }) {
                dao.insert(remote)
            }
        }
    }
}