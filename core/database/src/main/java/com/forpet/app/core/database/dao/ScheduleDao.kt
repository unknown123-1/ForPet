package com.forpet.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.forpet.app.core.database.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * 스케줄 테이블에 접근하는 DAO
 *
 * - Flow 반환 메서드: 데이터가 변경되면 자동으로 새 값을 방출한다.
 * - suspend 메서드: 쓰기 작업으로, 코루틴에서 호출해야 한다.
 */
@Dao
interface ScheduleDao {

    /** 특정 날짜의 비반복 스케줄 목록을 Flow로 반환 */
    @Query("SELECT * FROM schedules WHERE date = :epochDay AND repeat = 'NONE' ORDER BY isAllDay DESC, time ASC")
    fun getSchedulesByDate(epochDay: Long): Flow<List<ScheduleEntity>>

    /** 조회 날짜 이전에 시작된 반복 스케줄 목록을 Flow로 반환 */
    @Query("SELECT * FROM schedules WHERE date <= :epochDay AND repeat != 'NONE'")
    fun getRepeatingSchedulesBefore(epochDay: Long): Flow<List<ScheduleEntity>>

    /** 단건 조회 (편집 화면에서 기존 데이터 로드용) */
    @Query("SELECT * FROM schedules WHERE id = :id")
    fun getScheduleById(id: Long): Flow<ScheduleEntity?>

    /** 삽입 또는 수정 (id가 0이면 새로 생성, 기존 id면 업데이트) */
    @Upsert
    suspend fun upsertSchedule(entity: ScheduleEntity): Long

    /** 단건 조회 (suspend — 일회성 읽기용) */
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleByIdOnce(id: Long): ScheduleEntity?

    /** 단건 삭제 */
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: Long)
}
