package com.forpet.app.core.designsystem.component.bottomsheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val ANIMATION_DURATION = 350
private val DefaultBottomSheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
private val HandleHeight = 29.dp // top(16) + handle(5) + bottom(8)

/**
 * ForPetDraggableBottomSheet: ForPet 앱 전반에서 사용되는 커스텀 드래그 바텀 시트
 *
 * @param fullHeight 시트가 최대로 확장되었을 때의 높이
 * @param peekHeight 시트가 축소되었을 때 하단에 노출되는 높이
 * @param modifier Modifier
 * @param shape 바텀 시트의 모양 (기본값: 상단 둥근 모서리 32dp)
 * @param backgroundColor 바텀 시트의 배경색 (기본값: colors.surface)
 * @param content 바텀 시트 내부 컨텐츠.
 *                (isExpanded: Boolean) -> Unit 형태로 확장 여부를 전달받아 UI를 변경할 수 있음.
 */
@Composable
fun ForPetDraggableBottomSheet(
    fullHeight: Dp,
    peekHeight: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = DefaultBottomSheetShape,
    backgroundColor: Color = LocalForPetColors.current.surface,
    content: @Composable (isExpanded: Boolean, onToggleExpand: () -> Unit) -> Unit
) {
    // 확장 상태 (true: 확장됨, false: 축소됨)
    var expanded by remember { mutableStateOf(false) }

    // 📊 애니메이션을 위한 오프셋 상태 (0f = 확장된 상태의 위치)
    // Animatable: 값을 1:1로 정밀하게 제어할 수 있는 홀더입니다. (snapTo, animateTo 모두 가능)
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // 📏 Px 단위 변환 및 최대 이동 거리 계산
    val fullHeightPx = with(density) { fullHeight.toPx() }
    val peekHeightPx = with(density) { peekHeight.toPx() }
    // maxOffsetPx: 시트가 완전히 접히기 위해 아래로 내려가야 하는 총 거리
    val maxOffsetPx = fullHeightPx - peekHeightPx

    val sheetOffset = remember { Animatable(maxOffsetPx) }

    // 🎨 드래그 진행률에 따른 컨텐츠 투명도 계산 (Fade Out -> Fade In)
    // progress: 0.0(확장) ~ 1.0(축소) 사이의 값
    val progress = if (maxOffsetPx > 0) (sheetOffset.value / maxOffsetPx).coerceIn(0f, 1f) else 0f
    
    // contentAlpha: 중간(0.5)에서 0이 되고, 양 끝(0, 1)에서 1이 되는 공식
    // abs(progress - 0.5) * 2 -> 0.5일 때 0, 0일 때 1, 1일 때 1
    val contentAlpha = (kotlin.math.abs(progress - 0.5f) * 2).coerceIn(0f, 1f)

    val isContentExpanded = sheetOffset.value < (maxOffsetPx / 2)

    // 현재 보이는 영역의 높이 (핸들 제외)
    // 축소 상태에서는 peekHeight - handle, 확장 상태에서는 fullHeight - handle
    val visibleContentHeight = with(density) {
        (fullHeightPx - sheetOffset.value).coerceAtLeast(0f).toDp()
    } - HandleHeight

    // 🚀 LaunchedEffect: Compose에서 부수 효과(Side Effect)를 처리하는 함수입니다.
    // - key(expanded, maxOffsetPx): 이 변수들이 변할 때마다 블록 내부의 코드를 다시 실행합니다.
    // - 즉, 사용자가 버튼을 눌러 expanded 상태가 바뀌거나, 화면 크기가 변해 maxOffsetPx가 바뀌면 애니메이션을 실행합니다.
    LaunchedEffect(expanded, maxOffsetPx) {
        if (maxOffsetPx > 0) {
            // 목표 위치 계산 (확장되면 0, 축소되면 최대 오프셋)
            val targetOffset = if (expanded) 0f else maxOffsetPx
            
            // 💡 현재 위치와 목표 위치가 다를 때만 애니메이션 실행 (중복 실행 방지)
            if (kotlin.math.abs(sheetOffset.value - targetOffset) > 1f) {
                // animateTo: 현재 값에서 목표 값까지 부드럽게 값을 변경합니다.
                // 이 함수는 'suspend function'이라서 코루틴 스코프(LaunchedEffect) 안에서만 호출 가능합니다.
                sheetOffset.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
        }
    }

    // 👆 draggableState: 드래그 중 발생하는 변화량(delta)을 처리하는 상태 객체입니다.
    val draggableState = rememberDraggableState { delta ->
        // delta: 손가락이 움직인 거리 (위쪽은 -, 아래쪽은 +)
        // newOffset: 현재 위치 + 이동 거리. 단, 0(최대 확장) ~ maxOffsetPx(최대 축소) 범위를 벗어나지 않게 제한(coerceIn)합니다.
        val newOffset = (sheetOffset.value + delta).coerceIn(0f, maxOffsetPx)
        
        // ⚡ snapTo: 애니메이션 없이 값을 즉시 변경합니다. 드래그는 반응이 빨라야 하므로 animateTo 대신 사용합니다.
        coroutineScope.launch { sheetOffset.snapTo(newOffset) }
    }

    // UI 렌더링
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(fullHeight) // 높이는 전체 크기로 고정
            .offset { IntOffset(0, sheetOffset.value.roundToInt()) } // 위치만 이동
            .clip(shape)
            .background(backgroundColor)
            .draggable(
                state = draggableState, // 위에서 정의한 드래그 처리 로직 연결
                orientation = Orientation.Vertical, // 수직(위아래) 드래그만 허용
                
                // ✋ onDragStopped: 사용자가 손가락을 뗐을 때 호출됩니다.
                // velocity: 손을 뗄 때의 속도 (빠르게 튕겼는지 판단)
                onDragStopped = { velocity ->
                    // 🎯 목표 위치 결정 (관성 및 자석 효과)
                    val targetOffset = when {
                        velocity > 1000f -> maxOffsetPx // 아래로 빠르게 휙! -> 축소 상태로 이동
                        velocity < -1000f -> 0f         // 위로 빠르게 휙! -> 확장 상태로 이동
                        else -> if (sheetOffset.value > maxOffsetPx / 2) maxOffsetPx else 0f // 천천히 놓으면 가까운 쪽으로 이동
                    }

                    coroutineScope.launch {
                        // animateTo: 결정된 목표 위치까지 부드럽게 이동 (initialVelocity를 전달하여 관성 유지)
                        sheetOffset.animateTo(
                            targetValue = targetOffset,
                            initialVelocity = velocity,
                            animationSpec = tween(
                                durationMillis = ANIMATION_DURATION,
                                easing = androidx.compose.animation.core.FastOutSlowInEasing
                            )
                        )
                        // 애니메이션 완료 후 논리적 상태(expanded) 동기화
                        expanded = (targetOffset == 0f)
                    }
                }
            )
    ) {
        Column(Modifier.fillMaxWidth()) {
            BottomSheetHandle()

            // 콘텐츠 영역: 현재 보이는 높이에 맞게 제한
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(visibleContentHeight.coerceAtLeast(0.dp))
                    .alpha(contentAlpha),
            ) {
                content(isContentExpanded) { expanded = !expanded }
            }
        }
    }
}

/**
 * BottomSheetHandle: 바텀 시트 상단의 회색 손잡이 UI
 */
@Composable
private fun BottomSheetHandle() {
    val colors = LocalForPetColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 60.dp, height = 5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(colors.inactive)
        )
    }
}
