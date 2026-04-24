package com.forpet.app.core.data.di

import com.forpet.app.core.data.repository.OfflinePetRepository
import com.forpet.app.core.data.repository.OfflineScheduleRepository
import com.forpet.app.core.data.repository.OfflineWalkRepository
import com.forpet.app.core.data.repository.PetRepository
import com.forpet.app.core.data.repository.ScheduleRepository
import com.forpet.app.core.data.repository.WalkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsScheduleRepository(
        repository: OfflineScheduleRepository,
    ): ScheduleRepository

    @Binds
    abstract fun bindsWalkRepository(
        repository: OfflineWalkRepository,
    ): WalkRepository

    @Binds
    abstract fun bindsPetRepository(
        repository: OfflinePetRepository,
    ): PetRepository
}
