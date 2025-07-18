package com.xai.dosify.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xai.dosify.core.data.dao.*
import com.xai.dosify.core.data.models.*

@Database(entities = [Medication::class, DoseSchedule::class, DoseLog::class, Supply::class, Reconstitution::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun doseScheduleDao(): DoseScheduleDao
    abstract fun doseLogDao(): DoseLogDao
    abstract fun supplyDao(): SupplyDao
    abstract fun reconstitutionDao(): ReconstitutionDao
}