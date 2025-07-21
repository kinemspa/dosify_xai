package com.xai.dosify.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xai.dosify.core.data.models.Supply
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supply: Supply)

    @Update
    suspend fun update(supply: Supply)

    @Delete
    suspend fun delete(supply: Supply)

    @Query("SELECT * FROM supplies WHERE id = :id")
    fun getById(id: Long): Flow<Supply?>

    @Query("SELECT * FROM supplies")
    fun getAll(): Flow<List<Supply>>

    @Query("UPDATE supplies SET stock = stock - :amount WHERE id = :supplyId AND stock >= :amount")
    suspend fun decrementStock(supplyId: Long, amount: Double): Int
}