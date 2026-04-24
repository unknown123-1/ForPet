/**
 * 홈 화면
 *
 * 파일 구조:
 * - HomeScreen.kt         → Route 진입점 + 화면 본체 (이 파일)
 * - PetProfileSection.kt  → 펫 프로필 섹션
 * - WalkSection.kt        → 산책 정보 섹션
 * - WalkTrackingService.kt → GPS 백그라운드 서비스
 * - navigation/HomeNavigation.kt → 네비게이션 확장 함수
 */
package com.forpet.app.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forpet.app.core.designsystem.component.bottomsheet.ForPetDraggableBottomSheet
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.model.Pet

@Composable
fun HomeScreen(
    onNavigateToWalkResult: (sessionId: Long) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToEdit: (petId: Long) -> Unit = {},
    bottomSheetContent: @Composable (isExpanded: Boolean, onToggleExpand: () -> Unit) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val walkUiState by viewModel.walkUiState.collectAsStateWithLifecycle()
    val todayStats by viewModel.todayStats.collectAsStateWithLifecycle()
    val pet by viewModel.pet.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { sessionId ->
            onNavigateToWalkResult(sessionId)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.startWalk()
    }

    HomeScreen(
        walkUiState = walkUiState,
        todayStats = todayStats,
        pet = pet,
        onNavigateToEdit = onNavigateToEdit,
        onStartWalk = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                viewModel.startWalk()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
            }
        },
        onStopWalk = viewModel::stopWalk,
        onNavigateToRegister = onNavigateToRegister,
        bottomSheetContent = bottomSheetContent,
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    walkUiState: WalkUiState = WalkUiState(),
    todayStats: TodayWalkStats? = null,
    pet: Pet? = null,
    onStartWalk: () -> Unit = {},
    onStopWalk: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToEdit: (petId: Long) -> Unit = {},
    bottomSheetContent: @Composable (isExpanded: Boolean, onToggleExpand: () -> Unit) -> Unit = { _, _ -> },
) {
    val density = LocalDensity.current
    var topContentHeight by remember { mutableStateOf(0.dp) }

    val colors = LocalForPetColors.current
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        val screenHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    topContentHeight = with(density) { coordinates.size.height.toDp() }
                },
        ) {
            PetProfileSection(
                pet = pet,
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToEdit = onNavigateToEdit,
                modifier = Modifier.fillMaxWidth(),
            )
            WalkSection(
                walkUiState = walkUiState,
                todayStats = todayStats,
                modifier = Modifier.fillMaxWidth(),
                onStartWalk = onStartWalk,
                onStopWalk = onStopWalk,
            )
        }

        val fullHeight = screenHeight * 0.92f
        val peekHeight = if (topContentHeight > 0.dp) {
            (screenHeight - topContentHeight - 8.dp).coerceAtLeast(100.dp)
        } else {
            screenHeight * 0.35f
        }

        ForPetDraggableBottomSheet(
            fullHeight = fullHeight,
            peekHeight = peekHeight,
            modifier = Modifier.align(Alignment.BottomCenter),
            content = bottomSheetContent,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    ForPetTheme {
        HomeScreen(walkUiState = WalkUiState())
    }
}
