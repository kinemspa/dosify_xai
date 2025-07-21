package com.xai.core.data.repository

import com.xai.core.data.models.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun insert(profile: Profile)
    fun getAll(): Flow<List<Profile>>
    fun getPremium(): Flow<Profile?>
    suspend fun syncWithFirestore(userId: String)
}