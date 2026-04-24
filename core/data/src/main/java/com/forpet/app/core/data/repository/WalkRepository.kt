package com.forpet.app.core.data.repository

import com.forpet.app.core.model.WalkPoint
import com.forpet.app.core.model.WalkSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WalkRepository {

    /** 새 세션을 DB에 삽입하고 생성된 id 반환 */
    suspend fun startSession(): Long

    /** 세션 통계를 업데이트하고 endedAt을 기록 */
    suspend fun finishSession(
        sessionId: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float,
        durationSeconds: Long,
    )

    /** GPS 포인트 저장 */
    suspend fun addPoint(sessionId: Long, latitude: Double, longitude: Double)

    /** 특정 세션 Flow */
    fun observeSession(sessionId: Long): Flow<WalkSession?>

    /** 가장 최근 완료 세션 Flow (홈 화면 표시용) */
    fun observeLatestSession(): Flow<WalkSession?>

    /** 세션의 경로 포인트 목록 Flow */
    fun observeSessionPoints(sessionId: Long): Flow<List<WalkPoint>>

    /** 특정 날짜에 완료된 산책 세션 목록 Flow */
    fun observeSessionsByDate(date: LocalDate): Flow<List<WalkSession>>

    /** 미완료 세션 조회 (endedAt = null). 앱 재시작 시 정리용. */
    suspend fun getAbandonedSession(): WalkSession?

    /** 세션 포인트 일회성 조회 */
    suspend fun getSessionPointsOnce(sessionId: Long): List<WalkPoint>

    /** 완료된 전체 세션 목록 Flow (총 산책 거리 등 집계용) */
    fun observeAllCompletedSessions(): Flow<List<WalkSession>>

    /** 세션 삭제 (포인트는 CASCADE로 자동 삭제) */
    suspend fun deleteSession(sessionId: Long)
}
