package com.xai.dosify.core.com.xai.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xai.dosify.core.com.xai.core.data.models.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)

    @Query("SELECT * FROM profiles")
    fun getAll(): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE isPremium = 1 LIMIT 1")
    fun getPremium(): Flow<Profile?>
}