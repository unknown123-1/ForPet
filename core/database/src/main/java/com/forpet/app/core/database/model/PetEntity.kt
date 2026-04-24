package com.forpet.app.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.forpet.app.core.model.Pet
import java.time.LocalDate

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthdayEpochDay: Long? = null,
    val firstMetDayEpochDay: Long? = null,
    val photoUri: String? = null,
    val walkGoalKm: Int = 3,
)

fun PetEntity.asExternalModel(): Pet = Pet(
    id = id,
    name = name,
    birthday = birthdayEpochDay?.let { LocalDate.ofEpochDay(it) },
    firstMetDay = firstMetDayEpochDay?.let { LocalDate.ofEpochDay(it) },
    photoUri = photoUri,
    walkGoalKm = walkGoalKm,
)

fun Pet.asEntity(): PetEntity = PetEntity(
    id = id,
    name = name,
    birthdayEpochDay = birthday?.toEpochDay(),
    firstMetDayEpochDay = firstMetDay?.toEpochDay(),
    photoUri = photoUri,
    walkGoalKm = walkGoalKm,
)
