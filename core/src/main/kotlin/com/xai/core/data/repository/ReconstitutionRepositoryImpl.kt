package com.xai.core.data.repository

import com.xai.core.data.dao.ReconstitutionDao
import com.xai.core.data.models.Reconstitution
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReconstitutionRepositoryImpl @Inject constructor(
    private val dao: ReconstitutionDao,
    private val firestore: FirebaseFirestore
) : ReconstitutionRepository {
    override suspend fun insert(reconst: Reconstitution) = dao.insert(reconst)

    override suspend fun update(reconst: Reconstitution) = dao.update(reconst)

    override fun getByMedId(medId: Long): Flow<Reconstitution?> = dao.getByMedId(medId)

    override suspend fun syncWithFirestore(userId: String) {
        val localReconsts = dao.getAll().first()
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