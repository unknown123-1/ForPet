package com.forpet.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ForPetInlineDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalForPetColors.current
    var displayMonth by remember(selectedDate) {
        mutableStateOf(
            selectedDate?.let { YearMonth.of(it.year, it.month) } ?: YearMonth.now()
        )
    }

    val firstDayOfMonth = displayMonth.atDay(1)
    val daysInMonth = displayMonth.lengthOfMonth()
    // 한국 달력: 일요일 = 0, 월요일 = 1 ... 토요일 = 6
    val firstDayOffset = when (firstDayOfMonth.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        else -> 0
    }

    Column(modifier = modifier) {
        // 월 탐색 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { displayMonth = displayMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 달",
                    tint = colors.textPrimary,
                )
            }
            Text(
                text = "${displayMonth.year}년 ${displayMonth.monthValue}월",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
            )
            IconButton(onClick = { displayMonth = displayMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "다음 달",
                    tint = colors.textPrimary,
                )
            }
        }

        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    color = if (index == 0 || index == 6) colors.primary else colors.textPrimary,
                    fontWeight = FontWeight.Normal,
                )
            }
        }

        // 날짜 셀 구성
        val cells = buildList<LocalDate?> {
            repeat(firstDayOffset) { add(null) }
            for (day in 1..daysInMonth) add(displayMonth.atDay(day))
            while (size % 7 != 0) add(null)
        }

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
            ) {
                week.forEachIndexed { colIndex, date ->
                    val isWeekend = colIndex == 0 || colIndex == 6
                    val isSelected = date != null && date == selectedDate

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.primary else Color.Transparent)
                            .then(
                                if (date != null) Modifier.clickable { onDateSelected(date) }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (date != null) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                fontSize = 14.sp,
                                color = when {
                                    isSelected -> colors.onPrimary
                                    isWeekend -> colors.primary
                                    else -> colors.textPrimary
                                },
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}
