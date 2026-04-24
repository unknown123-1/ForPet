package com.forpet.app.core.model

import java.time.LocalDate
import java.time.LocalTime

enum class ScheduleType(val label: String) {
    CUSTOM("직접 입력"),
    HOSPITAL("병원"),
    VACCINE("접종"),
    MEDICATION("투약"),
    DEWORMING("구충"),
    BEAUTY("미용"),
    BATH("목욕"),
    ANNIVERSARY("기념"),
}

enum class RepeatType(val label: String) {
    NONE("반복 안 함"),
    DAILY("매일"),
    WEEKLY("매주"),
    BIWEEKLY("격주"),
    EVERY_30_DAYS("30일"),
    MONTHLY("1개월"),
}

data class Schedule(
    val id: Long = 0,
    val title: String,
    val memo: String = "",
    val type: ScheduleType,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.NOON,
    val isAllDay: Boolean = false,
    val repeat: RepeatType = RepeatType.NONE,
    val isDone: Boolean = false,
    val excludedDates: Set<LocalDate> = emptySet(),
)
