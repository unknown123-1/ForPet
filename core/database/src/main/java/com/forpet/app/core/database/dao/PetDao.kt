package com.forpet.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.forpet.app.core.database.model.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("SELECT * FROM pets LIMIT 1")
    fun observeFirstPet(): Flow<PetEntity?>

    @Query("SELECT * FROM pets")
    fun observeAllPets(): Flow<List<PetEntity>>

    @Upsert
    suspend fun upsertPet(entity: PetEntity): Long

    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
