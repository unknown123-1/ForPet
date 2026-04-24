package com.forpet.app.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forpet.app.core.designsystem.R as DesignR
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.designsystem.theme.Red
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private const val DAILY_GOAL_METERS = 4_000f

@Composable
internal fun WalkSection(
    walkUiState: WalkUiState,
    todayStats: TodayWalkStats?,
    modifier: Modifier = Modifier,
    onStartWalk: () -> Unit = {},
    onStopWalk: () -> Unit = {},
) {
    val colors = LocalForPetColors.current
    val isRunning = walkUiState.isRunning

    val minutes: Long
    val distanceMeters: Float
    val subtitleText: String
    when {
        isRunning -> {
            minutes = walkUiState.elapsedSeconds / 60
            distanceMeters = walkUiState.distanceMeters
            subtitleText = "산책 중"
        }
        todayStats != null -> {
            minutes = todayStats.totalSeconds / 60
            distanceMeters = todayStats.totalDistanceMeters
            subtitleText = formatLastUpdateTime(todayStats.lastEndedAt)
        }
        else -> {
            minutes = 0L
            distanceMeters = 0f
            subtitleText = ""
        }
    }

    val progress = (distanceMeters / DAILY_GOAL_METERS).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_dog_walk),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(25.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "오늘의 산책",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 16.sp,
                )
                if (subtitleText.isNotEmpty()) {
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 14.sp,
                    )
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$minutes",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 32.sp,
                )
                Text(text = " 분", fontSize = 13.sp, color = colors.textSecondary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formatDistanceValue(distanceMeters),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 32.sp,
                )
                Text(text = " km", fontSize = 13.sp, color = colors.textSecondary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        WalkProgressBar(progress = progress, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = if (isRunning) onStopWalk else onStartWalk,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Red else colors.primary,
            ),
            shape = RoundedCornerShape(22.5.dp),
        ) {
            Text(
                text = if (isRunning) "산책 종료하기" else "산책 시작하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onPrimary,
            )
        }
    }
}

private val THUMB_SIZE = 14.dp
private val TRACK_HEIGHT = 6.dp

@Composable
private fun WalkProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = LocalForPetColors.current

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End,
            ) {
                Box(
                    modifier = Modifier
                        .background(colors.textPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 3.dp),
                ) {
                    Text(
                        text = "4km",
                        color = colors.surface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                val tooltipColor = colors.textPrimary
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                ) {
                    val thumbHalfPx = (THUMB_SIZE / 2).toPx()
                    val tipX = size.width - thumbHalfPx
                    val halfBase = 2.dp.toPx()
                    val path = Path().apply {
                        moveTo(tipX - halfBase, 0f)
                        lineTo(tipX + halfBase, 0f)
                        lineTo(tipX, size.height)
                        close()
                    }
                    drawPath(path, color = tooltipColor)
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(THUMB_SIZE),
        ) {
            val trackWidthPx = constraints.maxWidth.toFloat()
            val thumbSizePx = with(LocalDensity.current) { THUMB_SIZE.toPx() }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TRACK_HEIGHT)
                    .clip(RoundedCornerShape(TRACK_HEIGHT / 2))
                    .background(colors.inactive)
                    .align(Alignment.Center),
            )

            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(TRACK_HEIGHT)
                        .clip(RoundedCornerShape(TRACK_HEIGHT / 2))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(colors.primary, colors.primaryGradient),
                            )
                        )
                        .align(Alignment.CenterStart),
                )
            }

            val thumbOffsetX = ((trackWidthPx * progress) - thumbSizePx / 2f)
                .coerceIn(0f, trackWidthPx - thumbSizePx)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(thumbOffsetX.roundToInt(), 0) }
                    .size(THUMB_SIZE)
                    .background(colors.primaryGradient, CircleShape),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(THUMB_SIZE)
                    .background(colors.inactive, CircleShape),
            )
        }
    }
}

private fun formatLastUpdateTime(epochMs: Long?): String {
    if (epochMs == null) return ""
    val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault())
    return DateTimeFormatter.ofPattern("a h시 mm분", Locale.KOREAN).format(ldt) + " 업데이트"
}

private fun formatDistanceValue(meters: Float): String = "%.1f".format(meters / 1000f)

@Preview(showBackground = true)
@Composable
private fun WalkSectionIdlePreview() {
    ForPetTheme { WalkSection(walkUiState = WalkUiState(), todayStats = null) }
}

@Preview(showBackground = true)
@Composable
private fun WalkSectionWithStatsPreview() {
    ForPetTheme {
        WalkSection(
            walkUiState = WalkUiState(),
            todayStats = TodayWalkStats(
                totalDistanceMeters = 2500f,
                totalSeconds = 1500L,
                lastEndedAt = System.currentTimeMillis(),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WalkSectionRunningPreview() {
    ForPetTheme {
        WalkSection(
            walkUiState = WalkUiState(isRunning = true, elapsedSeconds = 754, distanceMeters = 1230f),
            todayStats = null,
        )
    }
}
