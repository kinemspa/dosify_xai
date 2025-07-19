package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.MedicationDao
import com.xai.dosify.core.data.dao.ReconstitutionDao
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.core.data.models.Reconstitution
import com.xai.dosify.feature.advanced.utils.ReconstitutionUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MedicationRepository @Inject constructor(
    private val dao: MedicationDao,
    private val firestore: FirebaseFirestore,
    private val reconstDao: ReconstitutionDao
) {
    suspend fun insert(medication: Medication) = dao.insert(medication)

    suspend fun update(medication: Medication) = dao.update(medication)

    suspend fun delete(medication: Medication) = dao.delete(medication)

    fun getById(id: Long): Flow<Medication?> = dao.getById(id)

    fun getAll(): Flow<List<Medication>> = dao.getAll()

    suspend fun decrementStock(medId: Long, amount: Double): Boolean = dao.decrementStock(medId, amount) > 0

    suspend fun syncWithFirestore(userId: String) {
        val localMeds = dao.getAll().first()
        val remoteCollection = firestore.collection("users/$userId/meds")

        localMeds.forEach { med ->
            remoteCollection.document(med.id.toString()).set(med).await()
        }

        val remoteMeds = remoteCollection.get().await().toObjects(Medication::class.java)
        remoteMeds.forEach { remote ->
            if (!localMeds.any { it.id == remote.id }) {
                dao.insert(remote)
            }
        }
    }

    suspend fun saveWithReconstitution(med: Medication, powder: Double, solvent: Double) {
        dao.insert(med)
        val conc = ReconstitutionUtils.calculateConcentration(powder, solvent)
        val reconst = Reconstitution(medId = med.id, powderAmount = powder, solventVolume = solvent, desiredConcentration = conc)
        reconstDao.insert(reconst)
    }
}