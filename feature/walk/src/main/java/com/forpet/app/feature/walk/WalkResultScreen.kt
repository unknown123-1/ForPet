package com.forpet.app.feature.walk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.model.WalkPoint
import com.forpet.app.core.model.WalkSession
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WalkResultScreen(
    onDone: () -> Unit,
    viewModel: WalkResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is WalkResultUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is WalkResultUiState.Success -> {
            WalkResultContent(
                session = state.session,
                points = state.points,
                onDone = onDone,
            )
        }
    }
}

@Composable
private fun WalkResultContent(
    session: WalkSession,
    points: List<WalkPoint>,
    onDone: () -> Unit,
) {
    val density = LocalDensity.current
    val cameraPositionState = rememberCameraPositionState()

    var mapLoaded by remember { mutableStateOf(false) }
    var cardHeightDp by remember { mutableStateOf(0.dp) }

    val smoothedPoints = remember(points) {
        val latLngs = points.map { LatLng(it.latitude, it.longitude) }
        simplifyPolyline(latLngs, toleranceDegrees = 0.000035)
    }

    LaunchedEffect(smoothedPoints, mapLoaded) {
        if (!mapLoaded || smoothedPoints.size < 2) return@LaunchedEffect
        val builder = LatLngBounds.Builder()
        smoothedPoints.forEach { builder.include(it) }
        val bounds = builder.build()
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngBounds(bounds, 80)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(bottom = cardHeightDp),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                rotationGesturesEnabled = false,
                tiltGesturesEnabled = false,
            ),
            onMapLoaded = { mapLoaded = true },
        ) {
            if (smoothedPoints.size >= 2) {
                Polyline(
                    points = smoothedPoints,
                    width = 10f,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        WalkResultCard(
            session = session,
            onDone = onDone,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .onGloballyPositioned { coords ->
                    cardHeightDp = with(density) { coords.size.height.toDp() }
                },
        )
    }
}

/**
 * @param toleranceDegrees 위/경도 도(°) 단위 허용 오차.
 *   0.000035° ≈ 지표면 약 3~4m (위도 1° ≈ 111km 기준).
 *   값이 클수록 더 많은 점을 제거해 경로가 단순해지고,
 *   값이 작을수록 원본에 가깝게 유지된다.
 */
private fun simplifyPolyline(points: List<LatLng>, toleranceDegrees: Double): List<LatLng> {
    if (points.size <= 2) return points

    val end = points.size - 1
    var maxDist = 0.0
    var maxIdx = 0

    for (i in 1 until end) {
        val d = perpendicularDistance(points[i], points[0], points[end])
        if (d > maxDist) {
            maxDist = d
            maxIdx = i
        }
    }

    return if (maxDist > toleranceDegrees) {
        val left = simplifyPolyline(points.subList(0, maxIdx + 1), toleranceDegrees)
        val right = simplifyPolyline(points.subList(maxIdx, points.size), toleranceDegrees)
        left.dropLast(1) + right
    } else {
        listOf(points[0], points[end])
    }
}

/**
 * 점 p에서 선분 a-b까지의 수직 거리(도 단위).
 * 선분을 무한 직선이 아닌 선분으로 처리해 끝점 밖으로 나가지 않도록 t를 [0,1]로 클램핑.
 */
private fun perpendicularDistance(p: LatLng, a: LatLng, b: LatLng): Double {
    val dx = b.longitude - a.longitude
    val dy = b.latitude - a.latitude
    val lenSq = dx * dx + dy * dy
    if (lenSq == 0.0) {
        return Math.hypot(p.longitude - a.longitude, p.latitude - a.latitude)
    }
    val t = ((p.longitude - a.longitude) * dx + (p.latitude - a.latitude) * dy) / lenSq
    val tc = t.coerceIn(0.0, 1.0)
    return Math.hypot(p.longitude - (a.longitude + tc * dx), p.latitude - (a.latitude + tc * dy))
}

@Composable
private fun WalkResultCard(
    session: WalkSession,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateStr = remember(session.endedAt) {
        val ms = session.endedAt ?: session.startedAt
        SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).format(Date(ms))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "산책 완료",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ResultStatItem(
                    label = "거리",
                    value = formatResultDistance(session.distanceMeters),
                )
                ResultStatItem(
                    label = "시간",
                    value = formatResultDuration(session.durationSeconds),
                )
                ResultStatItem(
                    label = "평균 속도",
                    value = String.format("%.1f km/h", session.avgSpeedKmh),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text("완료", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ResultStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

private fun formatResultDistance(meters: Float): String {
    return if (meters < 1000f) "${meters.roundToInt()}m"
    else String.format("%.2fkm", meters / 1000f)
}

private fun formatResultDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "${h}시간 ${m}분"
    else if (m > 0) "${m}분 ${s}초"
    else "${s}초"
}

@Preview(showBackground = true)
@Composable
private fun WalkResultCardPreview() {
    ForPetTheme {
        WalkResultCard(
            session = WalkSession(
                id = 1,
                startedAt = System.currentTimeMillis() - 2_400_000,
                endedAt = System.currentTimeMillis(),
                distanceMeters = 1870f,
                avgSpeedKmh = 4.7f,
                durationSeconds = 1_440,
            ),
            onDone = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
