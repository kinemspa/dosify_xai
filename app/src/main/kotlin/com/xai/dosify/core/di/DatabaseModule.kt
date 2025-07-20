package com.xai.dosify.core.di

import android.content.Context
import androidx.room.Room
import com.xai.dosify.core.data.AppDatabase
import com.xai.dosify.core.data.dao.*
import com.xai.dosify.core.utils.BiometricUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = BiometricUtils.getPassphrase(context).toCharArray()
        val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase))
        return Room.databaseBuilder(context, AppDatabase::class.java, "dosify_db")
            .openHelperFactory(factory)
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideMedicationDao(db: AppDatabase): MedicationDao = db.medicationDao()
    @Provides
    fun provideDoseScheduleDao(db: AppDatabase): DoseScheduleDao = db.doseScheduleDao()
    @Provides
    fun provideDoseLogDao(db: AppDatabase): DoseLogDao = db.doseLogDao()
    @Provides
    fun provideSupplyDao(db: AppDatabase): SupplyDao = db.supplyDao()
    @Provides
    fun provideReconstitutionDao(db: AppDatabase): ReconstitutionDao = db.reconstitutionDao()
    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()
}