package com.forpet.app.core.data.repository

import com.forpet.app.core.database.dao.WalkPointDao
import com.forpet.app.core.database.dao.WalkSessionDao
import com.forpet.app.core.database.model.WalkPointEntity
import com.forpet.app.core.database.model.WalkSessionEntity
import com.forpet.app.core.model.WalkPoint
import com.forpet.app.core.model.WalkSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class OfflineWalkRepository @Inject constructor(
    private val sessionDao: WalkSessionDao,
    private val pointDao: WalkPointDao,
) : WalkRepository {

    override suspend fun startSession(): Long {
        val entity = WalkSessionEntity(startedAt = System.currentTimeMillis())
        return sessionDao.insert(entity)
    }

    override suspend fun finishSession(
        sessionId: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float,
        durationSeconds: Long,
    ) {
        val existing = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.update(
            existing.copy(
                endedAt = System.currentTimeMillis(),
                distanceMeters = distanceMeters,
                avgSpeedKmh = avgSpeedKmh,
                durationSeconds = durationSeconds,
            )
        )
    }

    override suspend fun addPoint(sessionId: Long, latitude: Double, longitude: Double) {
        pointDao.insert(
            WalkPointEntity(
                sessionId = sessionId,
                latitude = latitude,
                longitude = longitude,
                recordedAt = System.currentTimeMillis(),
            )
        )
    }

    override fun observeSession(sessionId: Long): Flow<WalkSession?> =
        sessionDao.observeSession(sessionId).map { it?.toDomain() }

    override fun observeLatestSession(): Flow<WalkSession?> =
        sessionDao.observeLatestSession().map { it?.toDomain() }

    override fun observeSessionPoints(sessionId: Long): Flow<List<WalkPoint>> =
        pointDao.observeSessionPoints(sessionId).map { list -> list.map { it.toDomain() } }

    override suspend fun getAbandonedSession(): WalkSession? =
        sessionDao.getActiveSession()?.toDomain()

    override suspend fun getSessionPointsOnce(sessionId: Long): List<WalkPoint> =
        pointDao.getSessionPoints(sessionId).map { it.toDomain() }

    override fun observeAllCompletedSessions(): Flow<List<WalkSession>> =
        sessionDao.observeAllCompletedSessions().map { list -> list.map { it.toDomain() } }

    override suspend fun deleteSession(sessionId: Long) =
        sessionDao.deleteSession(sessionId)

    override fun observeSessionsByDate(date: LocalDate): Flow<List<WalkSession>> {
        val zoneId = ZoneId.systemDefault()
        val startEpoch = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endEpoch = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return sessionDao.observeSessionsByDate(startEpoch, endEpoch)
            .map { list -> list.map { it.toDomain() } }
    }

    private fun WalkSessionEntity.toDomain() = WalkSession(
        id = id,
        startedAt = startedAt,
        endedAt = endedAt,
        distanceMeters = distanceMeters,
        avgSpeedKmh = avgSpeedKmh,
        durationSeconds = durationSeconds,
    )

    private fun WalkPointEntity.toDomain() = WalkPoint(
        id = id,
        sessionId = sessionId,
        latitude = latitude,
        longitude = longitude,
        recordedAt = recordedAt,
    )
}
