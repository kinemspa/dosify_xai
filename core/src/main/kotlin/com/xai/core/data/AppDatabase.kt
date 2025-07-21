package com.xai.dosify.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xai.dosify.core.data.dao.DoseLogDao
import com.xai.dosify.core.data.dao.DoseScheduleDao
import com.xai.dosify.core.data.dao.MedicationDao
import com.xai.dosify.core.data.dao.ProfileDao
import com.xai.dosify.core.data.dao.ReconstitutionDao
import com.xai.dosify.core.data.dao.SupplyDao
import com.xai.dosify.core.data.models.DoseLog
import com.xai.dosify.core.data.models.DoseSchedule
import com.xai.dosify.core.data.models.Medication
import com.xai.dosify.core.data.models.Profile
import com.xai.dosify.core.data.models.Reconstitution
import com.xai.dosify.core.data.models.Supply
import com.xai.dosify.core.data.dao.*
import com.xai.dosify.core.data.models.*

@Database(entities = [Medication::class, DoseSchedule::class, DoseLog::class, Supply::class, Reconstitution::class, Profile::class], version = 2, exportSchema = true)  // Bump version, add migration if needed
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun doseScheduleDao(): DoseScheduleDao
    abstract fun doseLogDao(): DoseLogDao
    abstract fun supplyDao(): SupplyDao
    abstract fun reconstitutionDao(): ReconstitutionDao
    abstract fun profileDao(): ProfileDao
}