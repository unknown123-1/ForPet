package com.forpet.app.core.database.di

import com.forpet.app.core.database.ForPetDatabase
import com.forpet.app.core.database.dao.PetDao
import com.forpet.app.core.database.dao.ScheduleDao
import com.forpet.app.core.database.dao.WalkPointDao
import com.forpet.app.core.database.dao.WalkSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesScheduleDao(database: ForPetDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun providesWalkSessionDao(database: ForPetDatabase): WalkSessionDao = database.walkSessionDao()

    @Provides
    fun providesWalkPointDao(database: ForPetDatabase): WalkPointDao = database.walkPointDao()

    @Provides
    fun providesPetDao(database: ForPetDatabase): PetDao = database.petDao()
}
