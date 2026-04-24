package com.forpet.app.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * GPS 경로 포인트 엔티티.
 * sessionId: WalkSessionEntity.id 참조
 */
@Entity(
    tableName = "walk_points",
    foreignKeys = [
        ForeignKey(
            entity = WalkSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class WalkPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: Long,  // epoch ms
)
