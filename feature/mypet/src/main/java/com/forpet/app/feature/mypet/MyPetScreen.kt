package com.forpet.app.feature.mypet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
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
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.model.Pet
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.forpet.app.core.designsystem.R as DesignSystemR

@Composable
fun MyPetScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToEdit: (petId: Long) -> Unit = {},
    onNavigateToPhotoCrop: (uri: String, petId: Long) -> Unit = { _, _ -> },
    croppedPhotoPath: String? = null,
    onCroppedPhotoConsumed: () -> Unit = {},
    viewModel: MyPetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(croppedPhotoPath) {
        croppedPhotoPath?.let { path ->
            viewModel.updatePhoto(path)
            onCroppedPhotoConsumed()
        }
    }

    val colors = LocalForPetColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .statusBarsPadding(),
    ) {
        when (val state = uiState) {
            MyPetUiState.Loading -> Box(Modifier.fillMaxSize())
            MyPetUiState.NoPet -> MyPetEmptyContent(onRegister = onNavigateToRegister)
            is MyPetUiState.HasPet -> MyPetContent(
                state = state,
                onEdit = { onNavigateToEdit(state.pet.id) },
                onNavigateToPhotoCrop = onNavigateToPhotoCrop,
                onResetPhoto = viewModel::resetPhoto,
            )
        }
    }
}

@Composable
private fun MyPetEmptyContent(onRegister: () -> Unit) {
    val colors = LocalForPetColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LogoHeader()

        Spacer(modifier = Modifier.height(52.dp))

        Text(
            text = "나의 펫을 등록하고\n소중한 일정을 기록해 보세요!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(colors.primary)
                .clickable(onClick = onRegister)
                .padding(horizontal = 28.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "펫 정보 등록하기",
                color = colors.onPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = colors.onPrimary,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                painter = painterResource(R.drawable.img_mypet_preview),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
            )
        }
    }
}

@Composable
private fun MyPetContent(
    state: MyPetUiState.HasPet,
    onEdit: () -> Unit,
    onNavigateToPhotoCrop: (uri: String, petId: Long) -> Unit,
    onResetPhoto: () -> Unit,
) {
    val colors = LocalForPetColors.current
    val pet = state.pet
    val today = LocalDate.now()

    var showPhotoSheet by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { onNavigateToPhotoCrop(it.toString(), pet.id) }
    }

    if (showPhotoSheet) {
        PetPhotoOptionSheet(
            hasPhoto = pet.photoUri != null,
            onDismiss = { showPhotoSheet = false },
            onSelectFromAlbum = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            onResetToDefault = onResetPhoto,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LogoHeader()

        Box {
            if (pet.photoUri != null) {
                AsyncImage(
                    model = pet.photoUri,
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
                    .border(2.dp, colors.surface, CircleShape)
                    .clickable { showPhotoSheet = true },
                shape = CircleShape,
                color = colors.inactive,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "사진 변경",
                        tint = colors.onPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = pet.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )

        Spacer(modifier = Modifier.height(10.dp))

        EditChip(onClick = onEdit)

        Spacer(modifier = Modifier.height(20.dp))

        val ageText = pet.birthday?.let { birthday ->
            val p = Period.between(birthday, today)
            when {
                p.years > 0 -> "${p.years}년 ${p.months}개월 ${p.days}일"
                p.months > 0 -> "${p.months}개월 ${p.days}일"
                else -> "${p.days}일"
            }
        } ?: "-"

        val togetherDays = pet.firstMetDay?.let {
            "%,d일".format(ChronoUnit.DAYS.between(it, today))
        } ?: "-"

        val totalKm = state.totalDistanceMeters / 1000f
        val totalWalkKm = if (totalKm >= 1f) "%,.0fkm".format(totalKm) else "%.1fkm".format(totalKm)

        PetStatsCard(
            ageText = ageText,
            togetherDays = togetherDays,
            totalWalkKm = totalWalkKm,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(15.dp))

        WalkGoalRow(
            goalKm = pet.walkGoalKm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(15.dp))

        BasicInfoSection(pet = pet)

        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
private fun LogoHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_logo),
            contentDescription = "포펫 로고",
        )
    }
}

@Composable
private fun EditChip(onClick: () -> Unit) {
    val colors = LocalForPetColors.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.cardSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "펫 정보 수정",
            fontSize = 13.sp,
            color = colors.textSecondary,
            fontWeight = FontWeight.Medium,
        )
        Icon(
            imageVector = Icons.Default.Create,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(13.dp),
        )
    }
}

@Composable
private fun PetStatsCard(
    ageText: String,
    togetherDays: String,
    totalWalkKm: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalForPetColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.primary)
            .padding(vertical = 18.dp, horizontal = 8.dp)
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatColumn(iconRes = R.drawable.ic_birth, label = "태어난 지", value = ageText, modifier = Modifier.weight(1f))
        StatDivider()
        StatColumn(iconRes = R.drawable.ic_together, label = "함께한 지", value = togetherDays, modifier = Modifier.weight(1f))
        StatDivider()
        StatColumn(iconRes = DesignSystemR.drawable.ic_dog_walk, label = "함께 산책한 거리", value = totalWalkKm, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatColumn(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalForPetColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colors.onPrimary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = colors.onPrimary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StatDivider() {
    val colors = LocalForPetColors.current
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(colors.onPrimary.copy(alpha = 0.3f)),
    )
}

@Composable
private fun WalkGoalRow(goalKm: Int, modifier: Modifier = Modifier) {
    val colors = LocalForPetColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "하루 산책 목표",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onPrimaryContainer,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.primary)
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = "${goalKm}km",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onPrimary,
            )
        }
    }
}

@Composable
private fun BasicInfoSection(pet: Pet) {
    val colors = LocalForPetColors.current
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "기본 정보",
            fontSize = 13.sp,
            color = colors.textSecondary,
            fontWeight = FontWeight.Medium,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardSurface)
                .padding(horizontal = 16.dp),
        ) {
            InfoRow(label = "이름", value = pet.name)
            InfoDivider()
            InfoRow(
                label = "태어난 날",
                value = pet.birthday?.format(dateFormatter) ?: "-",
            )
            InfoDivider()
            InfoRow(
                label = "처음 만난 날",
                value = pet.firstMetDay?.format(dateFormatter) ?: "-",
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colors = LocalForPetColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 15.sp, color = colors.textPrimary)
        Text(
            text = value,
            fontSize = 15.sp,
            color = colors.textSecondary,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun InfoDivider() {
    val colors = LocalForPetColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.inactive),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyPetEmptyPreview() {
    ForPetTheme {
        MyPetEmptyContent(onRegister = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyPetContentPreview() {
    ForPetTheme {
        MyPetContent(
            state = MyPetUiState.HasPet(
                pet = Pet(
                    id = 1,
                    name = "페퍼",
                    birthday = LocalDate.of(2021, 11, 26),
                    firstMetDay = LocalDate.of(2022, 2, 11),
                    walkGoalKm = 4,
                ),
                totalDistanceMeters = 9_876_000f,
            ),
            onEdit = {},
            onNavigateToPhotoCrop = { _, _ -> },
            onResetPhoto = {},
        )
    }
}
