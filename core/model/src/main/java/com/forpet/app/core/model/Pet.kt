package com.forpet.app.core.model

import java.time.LocalDate

data class Pet(
    val id: Long = 0,
    val name: String,
    val birthday: LocalDate? = null,
    val firstMetDay: LocalDate? = null,
    val photoUri: String? = null,
    val walkGoalKm: Int = 3,
)
