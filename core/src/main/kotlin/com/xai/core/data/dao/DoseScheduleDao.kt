package com.xai.dosify.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xai.dosify.core.data.models.DoseSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: DoseSchedule)

    @Update
    suspend fun update(schedule: DoseSchedule)

    @Delete
    suspend fun delete(schedule: DoseSchedule)

    @Query("SELECT * FROM dose_schedules WHERE id = :id")
    fun getById(id: Long): Flow<DoseSchedule?>

    @Query("SELECT * FROM dose_schedules WHERE medId = :medId")
    fun getByMedId(medId: Long): Flow<List<DoseSchedule>>

    @Query("SELECT * FROM dose_schedules WHERE isActive = 1")
    fun getActive(): Flow<List<DoseSchedule>>

    @Query("SELECT * FROM dose_schedules")
    fun getAll(): Flow<List<DoseSchedule>>
}