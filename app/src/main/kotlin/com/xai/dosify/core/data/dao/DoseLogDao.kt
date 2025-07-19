package com.xai.dosify.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xai.dosify.core.data.models.DoseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: DoseLog)

    @Query("SELECT * FROM dose_logs WHERE scheduleId = :scheduleId ORDER BY takenTime DESC")
    fun getBySchedule(scheduleId: Long): Flow<List<DoseLog>>

    @Query("SELECT * FROM dose_logs ORDER BY takenTime DESC LIMIT 10")
    fun getRecent(): Flow<List<DoseLog>>

    @Query("SELECT * FROM dose_logs")
    fun getAll(): Flow<List<DoseLog>>
}