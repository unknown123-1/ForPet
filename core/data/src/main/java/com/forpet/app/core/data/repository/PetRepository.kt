package com.forpet.app.core.data.repository

import com.forpet.app.core.model.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun observeFirstPet(): Flow<Pet?>
    suspend fun savePet(pet: Pet): Long
    suspend fun deletePet(petId: Long)
}
