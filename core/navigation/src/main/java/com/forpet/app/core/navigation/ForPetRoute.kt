package com.forpet.app.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for ForPet app.
 */
sealed interface Route {
    // Bottom Navigation Tabs
    @Serializable
    data object Home : Route

    @Serializable
    data object Calendar : Route

    @Serializable
    data object MyPet : Route

    // Schedule Flow
    @Serializable
    data class AddSchedule(val selectedDate: String? = null) : Route

    @Serializable
    data class EditSchedule(val scheduleId: String) : Route

    // Pet Flow
    @Serializable
    data class PetRegister(val petId: Long = 0L) : Route  // 0 = 신규 등록, 양수 = 수정

    // Walk Flow
    @Serializable
    data class WalkResult(val sessionId: Long) : Route

    // Photo Crop Flow
    @Serializable
    data class PhotoCrop(val uri: String, val petId: Long) : Route
}
