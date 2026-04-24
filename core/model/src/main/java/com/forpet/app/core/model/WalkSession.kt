package com.forpet.app.core.model

/**
 * 산책 세션 도메인 모델.
 */
data class WalkSession(
    val id: Long = 0,
    val startedAt: Long,          // epoch ms
    val endedAt: Long? = null,    // null = 진행 중
    val distanceMeters: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val durationSeconds: Long = 0L,
)
