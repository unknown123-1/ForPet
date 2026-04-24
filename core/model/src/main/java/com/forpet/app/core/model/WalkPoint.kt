package com.forpet.app.core.model

/**
 * GPS 경로 포인트 도메인 모델.
 */
data class WalkPoint(
    val id: Long = 0,
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: Long,  // epoch ms
)
