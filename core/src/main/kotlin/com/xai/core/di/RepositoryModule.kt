package com.xai.core.di

import com.xai.dosify.core.data.repository.AuthRepository
import com.xai.dosify.core.data.repository.DoseLogRepository
import com.xai.dosify.core.data.repository.DoseScheduleRepository
import com.xai.dosify.core.data.repository.MedicationRepository
import com.xai.dosify.core.data.repository.ProfileRepository
import com.xai.dosify.core.data.repository.ReconstitutionRepository
import com.xai.dosify.core.data.repository.SupplyRepository
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
    abstract fun bindAuthRepository(repo: AuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDoseLogRepository(repo: DoseLogRepository): DoseLogRepository

    @Binds
    @Singleton
    abstract fun bindDoseScheduleRepository(repo: DoseScheduleRepository): DoseScheduleRepository

    @Binds
    @Singleton
    abstract fun bindMedicationRepository(repo: MedicationRepository): MedicationRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(repo: ProfileRepository): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindReconstitutionRepository(repo: ReconstitutionRepository): ReconstitutionRepository

    @Binds
    @Singleton
    abstract fun bindSupplyRepository(repo: SupplyRepository): SupplyRepository
}