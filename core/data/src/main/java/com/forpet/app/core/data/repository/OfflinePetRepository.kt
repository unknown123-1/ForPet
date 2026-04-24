package com.forpet.app.core.data.repository

import com.forpet.app.core.database.dao.PetDao
import com.forpet.app.core.database.model.asEntity
import com.forpet.app.core.database.model.asExternalModel
import com.forpet.app.core.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflinePetRepository @Inject constructor(
    private val petDao: PetDao,
) : PetRepository {

    override fun observeFirstPet(): Flow<Pet?> =
        petDao.observeFirstPet().map { it?.asExternalModel() }

    override suspend fun savePet(pet: Pet): Long =
        petDao.upsertPet(pet.asEntity())

    override suspend fun deletePet(petId: Long) =
        petDao.deleteById(petId)
}
