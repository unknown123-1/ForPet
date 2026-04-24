package com.forpet.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.forpet.app.core.database.model.WalkSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WalkSessionEntity): Long

    @Update
    suspend fun update(session: WalkSessionEntity)

    @Query("SELECT * FROM walk_sessions WHERE id = :id")
    fun observeSession(id: Long): Flow<WalkSessionEntity?>

    /** 가장 최근에 완료된 세션 (endedAt IS NOT NULL) */
    @Query("SELECT * FROM walk_sessions WHERE endedAt IS NOT NULL ORDER BY endedAt DESC LIMIT 1")
    fun observeLatestSession(): Flow<WalkSessionEntity?>

    /** ID로 단일 세션 조회 */
    @Query("SELECT * FROM walk_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WalkSessionEntity?

    /** 세션 삭제 (미완료 세션 정리용) */
    @Query("DELETE FROM walk_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    /** 진행 중인 세션 (endedAt IS NULL) */
    @Query("SELECT * FROM walk_sessions WHERE endedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveSession(): WalkSessionEntity?

    /** 특정 날짜에 완료된 세션 목록 */
    @Query("SELECT * FROM walk_sessions WHERE endedAt >= :startEpoch AND endedAt < :endEpoch ORDER BY endedAt ASC")
    fun observeSessionsByDate(startEpoch: Long, endEpoch: Long): Flow<List<WalkSessionEntity>>

    /** 완료된 전체 세션 목록 (총 산책 거리 계산용) */
    @Query("SELECT * FROM walk_sessions WHERE endedAt IS NOT NULL ORDER BY endedAt ASC")
    fun observeAllCompletedSessions(): Flow<List<WalkSessionEntity>>
}
