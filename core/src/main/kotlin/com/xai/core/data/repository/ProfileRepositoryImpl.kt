package com.xai.core.data.repository

import com.xai.core.data.dao.ProfileDao
import com.xai.core.data.models.Profile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao,
    private val firestore: FirebaseFirestore
) : ProfileRepository {
    override suspend fun insert(profile: Profile) = dao.insert(profile)

    override fun getAll(): Flow<List<Profile>> = dao.getAll()

    override fun getPremium(): Flow<Profile?> = dao.getPremium()

    override suspend fun syncWithFirestore(userId: String) {
        val localProfiles = dao.getAll().first()
        val remoteCollection = firestore.collection("users/$userId/profiles")

        localProfiles.forEach { profile ->
            remoteCollection.document(profile.id.toString()).set(profile).await()
        }

        val remoteProfiles = remoteCollection.get().await().toObjects(Profile::class.java)
        remoteProfiles.forEach { remote ->
            if (!localProfiles.any { it.id == remote.id }) {
                dao.insert(remote)
            }
        }
    }
}