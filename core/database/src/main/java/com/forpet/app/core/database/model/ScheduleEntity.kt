package com.forpet.app.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.forpet.app.core.model.RepeatType
import com.forpet.app.core.model.Schedule
import com.forpet.app.core.model.ScheduleType
import java.time.LocalDate
import java.time.LocalTime

/**
 * Room Entity — schedules 테이블의 한 행을 나타낸다.
 *
 * 도메인 모델(Schedule)과 1:1 대응하지만, DB 저장에 적합한 타입을 사용한다.
 * type/repeat는 String(enum name)으로, date/time은 Long으로 저장된다.
 */
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val memo: String,
    val type: String,
    val date: Long,
    val time: Long,
    val isAllDay: Boolean,
    val repeat: String,
    val isDone: Boolean,
    val excludedDates: String = "",
)

/** Entity → 도메인 모델 변환 */
fun ScheduleEntity.asExternalModel() = Schedule(
    id = id,
    title = title,
    memo = memo,
    type = ScheduleType.valueOf(type),
    date = LocalDate.ofEpochDay(date),
    time = LocalTime.ofSecondOfDay(time),
    isAllDay = isAllDay,
    repeat = RepeatType.valueOf(repeat),
    isDone = isDone,
    excludedDates = if (excludedDates.isBlank()) emptySet()
    else excludedDates.split(",").map { LocalDate.ofEpochDay(it.trim().toLong()) }.toSet(),
)

/** 도메인 모델 → Entity 변환 */
fun Schedule.asEntity() = ScheduleEntity(
    id = id,
    title = title,
    memo = memo,
    type = type.name,
    date = date.toEpochDay(),
    time = time.toSecondOfDay().toLong(),
    isAllDay = isAllDay,
    repeat = repeat.name,
    isDone = isDone,
    excludedDates = excludedDates.joinToString(",") { it.toEpochDay().toString() },
)
