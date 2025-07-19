package com.xai.dosify.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xai.dosify.core.data.models.Reconstitution
import kotlinx.coroutines.flow.Flow

@Dao
interface ReconstitutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reconst: Reconstitution)

    @Update
    suspend fun update(reconst: Reconstitution)

    @Query("SELECT * FROM reconstitutions WHERE medId = :medId")
    fun getByMedId(medId: Long): Flow<Reconstitution?>

    @Query("SELECT * FROM reconstitutions")
    fun getAll(): Flow<List<Reconstitution>>
}