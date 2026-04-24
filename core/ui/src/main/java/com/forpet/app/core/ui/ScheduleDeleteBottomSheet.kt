package com.forpet.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors

/**
 * 스케줄 삭제 확인 바텀시트
 *
 * @param isRepeatSchedule true이면 "이 할 일만 삭제" / "반복 모두 삭제" 두 옵션 표시
 * @param onDismiss 시트 닫기
 * @param onDelete 단일 삭제 (반복이 아닌 경우) 또는 이 할 일만 삭제 (반복인 경우)
 * @param onDeleteAll 반복되는 할 일 모두 삭제 (반복인 경우에만 사용)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDeleteBottomSheet(
    isRepeatSchedule: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit = {},
) {
    val colors = LocalForPetColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 20.dp)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(colors.inactive, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "이 할 일을 삭제할까요?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(35.dp))

            if (isRepeatSchedule) {
                DeleteActionButton(
                    text = "이 할 일만 삭제하기",
                    backgroundColor = colors.primary,
                    textColor = colors.onPrimary,
                    onClick = onDelete,
                )
                Spacer(modifier = Modifier.height(10.dp))
                DeleteActionButton(
                    text = "반복되는 할 일 모두 삭제하기",
                    backgroundColor = colors.cardSurface,
                    textColor = colors.primary,
                    onClick = onDeleteAll,
                )
            } else {
                DeleteActionButton(
                    text = "삭제하기",
                    backgroundColor = colors.primary,
                    textColor = colors.onPrimary,
                    onClick = onDelete,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = colors.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun DeleteActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ScheduleDeleteBottomSheetPreview() {
    ForPetTheme {
        ScheduleDeleteBottomSheet(
            isRepeatSchedule = false,
            onDismiss = {},
            onDelete = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ScheduleDeleteBottomSheetRepeatPreview() {
    ForPetTheme {
        ScheduleDeleteBottomSheet(
            isRepeatSchedule = true,
            onDismiss = {},
            onDelete = {},
            onDeleteAll = {},
        )
    }
}
