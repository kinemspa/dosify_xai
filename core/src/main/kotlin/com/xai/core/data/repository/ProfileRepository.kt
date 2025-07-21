package com.xai.dosify.core.data.repository

import com.xai.dosify.core.data.dao.ProfileDao
import com.xai.dosify.core.data.models.Profile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val dao: ProfileDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun insert(profile: Profile) = dao.insert(profile)

    fun getAll(): Flow<List<Profile>> = dao.getAll()

    fun getPremium(): Flow<Profile?> = dao.getPremium()

    suspend fun syncWithFirestore(userId: String) {
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