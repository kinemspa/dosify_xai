package com.xai.dosify.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xai.dosify.core.data.models.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: Medication)

    @Update
    suspend fun update(medication: Medication)

    @Delete
    suspend fun delete(medication: Medication)

    @Query("SELECT * FROM medications WHERE id = :id")
    fun getById(id: Long): Flow<Medication?>

    @Query("SELECT * FROM medications")
    fun getAll(): Flow<List<Medication>>

    @Query("UPDATE medications SET stock = stock - :amount WHERE id = :medId AND stock >= :amount")
    suspend fun decrementStock(medId: Long, amount: Double): Int
}