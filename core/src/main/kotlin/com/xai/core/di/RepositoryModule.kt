package com.xai.core.di

import com.xai.core.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDoseLogRepository(impl: DoseLogRepositoryImpl): DoseLogRepository

    @Binds
    @Singleton
    abstract fun bindDoseScheduleRepository(impl: DoseScheduleRepositoryImpl): DoseScheduleRepository

    @Binds
    @Singleton
    abstract fun bindMedicationRepository(impl: MedicationRepositoryImpl): MedicationRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindReconstitutionRepository(impl: ReconstitutionRepositoryImpl): ReconstitutionRepository

    @Binds
    @Singleton
    abstract fun bindSupplyRepository(impl: SupplyRepositoryImpl): SupplyRepository
}