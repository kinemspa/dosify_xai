package com.xai.core.data.repository

import com.xai.core.data.dao.MedicationDao
import com.xai.core.data.dao.ReconstitutionDao
import com.xai.core.data.models.Medication
import com.xai.core.data.models.Reconstitution
import com.xai.core.utils.ReconstitutionUtils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class MedicationRepositoryImpl @Inject constructor(
    private val dao: MedicationDao,
    private val firestore: FirebaseFirestore,
    private val reconstDao: ReconstitutionDao
) : MedicationRepository {
    override suspend fun insert(medication: Medication) = dao.insert(medication)

    override suspend fun update(medication: Medication) = dao.update(medication)

    override suspend fun delete(medication: Medication) = dao.delete(medication)

    override fun getById(id: Long): Flow<Medication?> = dao.getById(id)

    override fun getAll(): Flow<List<Medication>> = dao.getAll()

    override suspend fun decrementStock(medId: Long, amount: Double): Boolean = dao.decrementStock(medId, amount) > 0

    override suspend fun syncWithFirestore(userId: String) {
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

    override suspend fun saveWithReconstitution(med: Medication, powder: Double, solvent: Double) {
        dao.insert(med)
        val conc = ReconstitutionUtils.calculateConcentration(powder, solvent)
        val reconst = Reconstitution(medId = med.id, powderAmount = powder, solventVolume = solvent, desiredConcentration = conc)
        reconstDao.insert(reconst)
        Timber.d("Reconst inserted for med ${med.id}")
    }
}