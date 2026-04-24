package com.forpet.app.feature.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.model.Schedule
import com.forpet.app.core.model.ScheduleType
import com.forpet.app.core.model.WalkSession
import com.forpet.app.core.ui.ScheduleDeleteBottomSheet
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

/**
 * 캘린더 피처의 메인 화면(Route)입니다.
 * ViewModel을 주입받고, 전체적인 화면 레이아웃과 상태를 관리합니다.
 */
@Composable
fun CalendarScreen(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel(),
    onEditSchedule: (Long) -> Unit = {},
    onNavigateToWalkResult: (Long) -> Unit = {},
) {
    // 공개 범위 조정을 위해 일부 피처의 소스코드는 제외했습니다.
    // 현재 저장소에는 `mypet`, `home` 피처를 중심으로 구성한 코드가 포함되어 있습니다.
    // 전체 앱 동작은 GitHub Releases에 첨부된 APK를 통해 확인하실 수 있습니다.

}
