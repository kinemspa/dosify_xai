package com.xai.core.di

import android.content.Context
import androidx.room.Room
import com.xai.core.data.AppDatabase
import com.xai.core.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import com.xai.core.utils.BiometricUtils
import com.xai.core.data.MIGRATION_1_2
import android.content.SharedPreferences
import android.preference.PreferenceManager

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = BiometricUtils.getPassphrase(context)
        val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))
        return Room.databaseBuilder(context, AppDatabase::class.java, "dosify_db")
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration(true)
            .addMigrations(MIGRATION_1_2)
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

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}