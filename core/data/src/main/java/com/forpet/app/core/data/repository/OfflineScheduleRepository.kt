package com.forpet.app.core.data.repository

import com.forpet.app.core.database.dao.ScheduleDao
import com.forpet.app.core.database.model.asEntity
import com.forpet.app.core.database.model.asExternalModel
import com.forpet.app.core.model.RepeatType
import com.forpet.app.core.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Room DB 기반 ScheduleRepository 구현체
 *
 * DAO를 주입받아 Entity ↔ 도메인 모델 변환을 수행한다.
 * Flow를 반환하므로 DB 변경 시 UI가 자동으로 갱신된다.
 */
internal class OfflineScheduleRepository @Inject constructor(
    private val scheduleDao: ScheduleDao,
) : ScheduleRepository {

    override fun getSchedulesByDate(date: LocalDate): Flow<List<Schedule>> {
        val epochDay = date.toEpochDay()
        val nonRepeating = scheduleDao.getSchedulesByDate(epochDay)
            .map { entities -> entities.map { it.asExternalModel() } }
        val repeating = scheduleDao.getRepeatingSchedulesBefore(epochDay)
            .map { entities ->
                entities.map { it.asExternalModel() }
                    .filter { it.matchesDate(date) }
            }
        return combine(nonRepeating, repeating) { a, b ->
            (a + b).sortedWith(compareByDescending<Schedule> { it.isAllDay }.thenBy { it.time })
        }
    }

    override fun getScheduleById(id: Long): Flow<Schedule?> =
        scheduleDao.getScheduleById(id)
            .map { it?.asExternalModel() }

    override suspend fun saveSchedule(schedule: Schedule): Long =
        scheduleDao.upsertSchedule(schedule.asEntity())

    override suspend fun deleteSchedule(id: Long) =
        scheduleDao.deleteById(id)

    override suspend fun excludeDate(scheduleId: Long, date: LocalDate) {
        val entity = scheduleDao.getScheduleByIdOnce(scheduleId) ?: return
        val current = entity.excludedDates
        val epochDayStr = date.toEpochDay().toString()
        val updated = if (current.isBlank()) epochDayStr else "$current,$epochDayStr"
        scheduleDao.upsertSchedule(entity.copy(excludedDates = updated))
    }

    override suspend fun toggleDone(id: Long) {
        val entity = scheduleDao.getScheduleByIdOnce(id) ?: return
        scheduleDao.upsertSchedule(entity.copy(isDone = !entity.isDone))
    }
}

private fun Schedule.matchesDate(target: LocalDate): Boolean {
    if (target in excludedDates) return false
    val daysBetween = ChronoUnit.DAYS.between(date, target)
    if (daysBetween < 0) return false
    return when (repeat) {
        RepeatType.NONE -> false
        RepeatType.DAILY -> true
        RepeatType.WEEKLY -> daysBetween % 7 == 0L
        RepeatType.BIWEEKLY -> daysBetween % 14 == 0L
        RepeatType.EVERY_30_DAYS -> daysBetween % 30 == 0L
        RepeatType.MONTHLY -> target.dayOfMonth == date.dayOfMonth
    }
}
