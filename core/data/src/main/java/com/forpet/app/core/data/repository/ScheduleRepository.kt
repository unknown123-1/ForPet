package com.forpet.app.core.data.repository

import com.forpet.app.core.model.Schedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ScheduleRepository {

    /** 특정 날짜의 스케줄 목록 (Flow — 데이터 변경 시 자동 갱신) */
    fun getSchedulesByDate(date: LocalDate): Flow<List<Schedule>>

    /** 단건 조회 (편집 화면에서 기존 데이터 로드용) */
    fun getScheduleById(id: Long): Flow<Schedule?>

    /** 저장 (id=0이면 새로 생성, 기존 id면 수정). 생성된 id 반환 */
    suspend fun saveSchedule(schedule: Schedule): Long

    /** 단건 삭제 */
    suspend fun deleteSchedule(id: Long)

    /** 반복 일정에서 특정 날짜만 제외 */
    suspend fun excludeDate(scheduleId: Long, date: LocalDate)

    /** 완료 상태 토글 */
    suspend fun toggleDone(id: Long)
}
