package com.forpet.app.feature.mypet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.ui.ForPetBottomActionBar
import com.forpet.app.core.ui.ForPetClearButton
import com.forpet.app.core.ui.ForPetDatePickerDialog
import com.forpet.app.core.ui.ForPetIconLabel
import com.forpet.app.core.ui.ForPetRoundedButton
import com.forpet.app.core.ui.ForPetTextField
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import com.forpet.app.core.designsystem.R as DesignSystemR

@Composable
fun PetRegisterScreen(
    onNavigateUp: () -> Unit,
    onNavigateToPhotoCrop: (uri: String, petId: Long) -> Unit = { _, _ -> },
    croppedPhotoPath: String? = null,
    onCroppedPhotoConsumed: () -> Unit = {},
    viewModel: PetRegisterViewModel = hiltViewModel(),
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    LaunchedEffect(isSaved) {
        if (isSaved) onNavigateUp()
    }

    LaunchedEffect(croppedPhotoPath) {
        croppedPhotoPath?.let { path ->
            viewModel.updatePhotoUri(path)
            onCroppedPhotoConsumed()
        }
    }

    PetRegisterContent(
        formState = formState,
        onNavigateUp = onNavigateUp,
        onNameChange = viewModel::updateName,
        onBirthdaySelect = viewModel::updateBirthday,
        onFirstMetDaySelect = viewModel::updateFirstMetDay,
        onWalkGoalChange = viewModel::updateWalkGoal,
        onNavigateToPhotoCrop = onNavigateToPhotoCrop,
        onResetPhotoUri = viewModel::resetPhotoUri,
        onSave = viewModel::save,
    )
}

@Composable
private fun PetRegisterContent(
    formState: PetFormState,
    onNavigateUp: () -> Unit,
    onNameChange: (String) -> Unit,
    onBirthdaySelect: (LocalDate?) -> Unit,
    onFirstMetDaySelect: (LocalDate?) -> Unit,
    onWalkGoalChange: (Int) -> Unit,
    onNavigateToPhotoCrop: (uri: String, petId: Long) -> Unit,
    onResetPhotoUri: () -> Unit,
    onSave: () -> Unit,
) {
    val colors = LocalForPetColors.current
    var showBirthdayPicker by remember { mutableStateOf(false) }
    var showFirstMetDayPicker by remember { mutableStateOf(false) }
    var showPhotoSheet by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { onNavigateToPhotoCrop(it.toString(), formState.id) }
    }

    if (showBirthdayPicker) {
        ForPetDatePickerDialog(
            initialDateMillis = formState.birthday
                ?.atStartOfDay(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli(),
            onDismiss = { showBirthdayPicker = false },
            onConfirm = { millis ->
                val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                onBirthdaySelect(date)
                showBirthdayPicker = false
            },
        )
    }

    if (showFirstMetDayPicker) {
        ForPetDatePickerDialog(
            initialDateMillis = formState.firstMetDay
                ?.atStartOfDay(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli(),
            onDismiss = { showFirstMetDayPicker = false },
            onConfirm = { millis ->
                val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                onFirstMetDaySelect(date)
                showFirstMetDayPicker = false
            },
        )
    }

    if (showPhotoSheet) {
        PetPhotoOptionSheet(
            hasPhoto = formState.photoUri != null,
            onDismiss = { showPhotoSheet = false },
            onSelectFromAlbum = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            onResetToDefault = onResetPhotoUri,
        )
    }

    Scaffold(
        containerColor = colors.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기",
                        tint = colors.textPrimary,
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "펫 정보",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                    )
                }
                Spacer(modifier = Modifier.size(48.dp))
            }
        },
        bottomBar = {
            ForPetBottomActionBar(
                text = "저장하기",
                enabled = formState.name.isNotBlank(),
                onClick = onSave,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.clickable { showPhotoSheet = true },
            ) {
                if (formState.photoUri != null) {
                    AsyncImage(
                        model = formState.photoUri,
                        contentDescription = "펫 사진",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(colors.inactive),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(DesignSystemR.drawable.ic_pet_placeholder),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, colors.surface, CircleShape),
                    shape = CircleShape,
                    color = if (formState.photoUri != null) colors.inactive else colors.primary,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (formState.photoUri != null) Icons.Default.CameraAlt else Icons.Default.Add,
                            contentDescription = "사진 추가",
                            tint = colors.onPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ForPetTextField(
                label = "이름",
                value = formState.name,
                placeholder = "반려동물의 이름을 입력하세요.",
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                trailingContent = {
                    if (formState.name.isNotBlank()) {
                        ForPetClearButton(onClick = { onNameChange("") })
                    }
                },
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "한글 최대 6자",
                fontSize = 12.sp,
                color = colors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))
            SectionDivider()

            DatePickerRow(
                label = "태어난 날",
                date = formState.birthday,
                dateFormatter = dateFormatter,
                onPickerClick = { showBirthdayPicker = true },
            )

            SectionDivider()

            DatePickerRow(
                label = "처음 만난 날",
                date = formState.firstMetDay,
                dateFormatter = dateFormatter,
                onPickerClick = { showFirstMetDayPicker = true },
            )

            SectionDivider()

            WalkGoalSection(
                goalKm = formState.walkGoalKm,
                onGoalChange = onWalkGoalChange,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionDivider() {
    val colors = LocalForPetColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.inactive),
    )
}

@Composable
private fun DatePickerRow(
    label: String,
    date: LocalDate?,
    dateFormatter: DateTimeFormatter,
    onPickerClick: () -> Unit,
) {
    val colors = LocalForPetColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ForPetIconLabel(
            icon = Icons.Filled.CalendarMonth,
            iconTint = colors.primary,
            label = label,
        )
        ForPetRoundedButton(
            text = date?.format(dateFormatter) ?: "날짜 선택",
            onClick = onPickerClick,
        )
    }
}

@Composable
private fun WalkGoalSection(goalKm: Int, onGoalChange: (Int) -> Unit) {
    val colors = LocalForPetColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ForPetIconLabel(
            icon = Icons.Filled.Pets,
            iconTint = colors.primary,
            label = "하루 산책 목표",
        )

        Slider(
            value = goalKm.toFloat(),
            onValueChange = { onGoalChange(it.roundToInt()) },
            valueRange = 1f..6f,
            steps = 4,
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.inactive,
                activeTickColor = colors.primary,
                inactiveTickColor = colors.inactive,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            for (km in 1..6) {
                val isSelected = km == goalKm
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) colors.primary else Color.Transparent)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${km}km",
                        fontSize = 11.sp,
                        color = if (isSelected) colors.onPrimary else colors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PetRegisterEmptyPreview() {
    ForPetTheme {
        PetRegisterContent(
            formState = PetFormState(),
            onNavigateUp = {},
            onNameChange = {},
            onBirthdaySelect = {},
            onFirstMetDaySelect = {},
            onWalkGoalChange = {},
            onNavigateToPhotoCrop = { _, _ -> },
            onResetPhotoUri = {},
            onSave = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PetRegisterFilledPreview() {
    ForPetTheme {
        PetRegisterContent(
            formState = PetFormState(
                name = "페퍼",
                birthday = LocalDate.of(2021, 11, 26),
                firstMetDay = LocalDate.of(2022, 2, 11),
                walkGoalKm = 4,
            ),
            onNavigateUp = {},
            onNameChange = {},
            onBirthdaySelect = {},
            onFirstMetDaySelect = {},
            onWalkGoalChange = {},
            onNavigateToPhotoCrop = { _, _ -> },
            onResetPhotoUri = {},
            onSave = {},
        )
    }
}
