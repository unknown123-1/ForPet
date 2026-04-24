package com.forpet.app.core.database.di

import android.content.Context
import androidx.room.Room
import com.forpet.app.core.database.ForPetDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesForPetDatabase(
        @ApplicationContext context: Context,
    ): ForPetDatabase = Room.databaseBuilder(
        context,
        ForPetDatabase::class.java,
        "forpet-database",
    )
        .addMigrations(
            ForPetDatabase.MIGRATION_1_2,
            ForPetDatabase.MIGRATION_2_3,
            ForPetDatabase.MIGRATION_3_4,
            ForPetDatabase.MIGRATION_4_5,
            ForPetDatabase.MIGRATION_5_6,
        )
        .build()
}
