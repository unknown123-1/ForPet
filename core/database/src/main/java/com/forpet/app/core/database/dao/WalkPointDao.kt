package com.forpet.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.forpet.app.core.database.model.WalkPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: WalkPointEntity)

    @Query("SELECT * FROM walk_points WHERE sessionId = :sessionId ORDER BY recordedAt ASC")
    fun observeSessionPoints(sessionId: Long): Flow<List<WalkPointEntity>>

    /** 일회성 조회 (미완료 세션 거리 계산용) */
    @Query("SELECT * FROM walk_points WHERE sessionId = :sessionId ORDER BY recordedAt ASC")
    suspend fun getSessionPoints(sessionId: Long): List<WalkPointEntity>
}
