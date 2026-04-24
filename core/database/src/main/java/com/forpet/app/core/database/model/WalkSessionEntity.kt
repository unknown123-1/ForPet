package com.forpet.app.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 산책 세션 엔티티.
 * startedAt / endedAt: epoch milliseconds (UTC)
 */
@Entity(tableName = "walk_sessions")
data class WalkSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startedAt: Long,           // epoch ms
    val endedAt: Long? = null,     // null = 진행 중
    val distanceMeters: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val durationSeconds: Long = 0L,
)
