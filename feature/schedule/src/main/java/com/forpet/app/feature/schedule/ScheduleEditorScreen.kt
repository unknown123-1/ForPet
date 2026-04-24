package com.forpet.app.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.forpet.app.core.designsystem.component.ForPetTopAppBar
import com.forpet.app.core.designsystem.theme.Blue
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.Green
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.designsystem.theme.Orange
import com.forpet.app.core.designsystem.theme.Red
import com.forpet.app.core.ui.ForPetBottomActionBar
import com.forpet.app.core.ui.ForPetClearButton
import com.forpet.app.core.ui.ForPetDatePickerDialog
import com.forpet.app.core.ui.ForPetIconLabel
import com.forpet.app.core.ui.ForPetRoundedButton
import com.forpet.app.core.ui.ForPetTextField
import com.forpet.app.core.ui.ForPetTimePickerDialog
import com.forpet.app.core.ui.ScheduleDeleteBottomSheet
import com.forpet.app.core.model.RepeatType
import com.forpet.app.core.model.ScheduleType
import java.time.LocalDate
import java.time.LocalTime

/**
 * 일정 추가 화면의 Route-level Composable (Stateful)
 * ViewModel을 주입받아 상태를 수집하고, Stateless UI에 전달한다.
 */
@Composable
fun AddScheduleScreen(
    onNavigateUp: () -> Unit,
    viewModel: ScheduleEditorViewModel = hiltViewModel(),
) {
    // 공개 범위 조정을 위해 일부 피처의 소스코드는 제외했습니다.
    // 현재 저장소에는 `mypet`, `home` 피처를 중심으로 구성한 코드가 포함되어 있습니다.
    // 전체 앱 동작은 GitHub Releases에 첨부된 APK를 통해 확인하실 수 있습니다.
}
