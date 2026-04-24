package com.forpet.app.feature.mypet

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
import androidx.compose.material.icons.filled.CameraAlt
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
 * 펫 프로필 사진 변경 바텀 시트 — ScheduleDeleteBottomSheet 스타일 통일
 *
 * 옵션:
 *  1. 앨범에서 사진 선택 (항상 표시)
 *  2. 기본 이미지 적용 (사진이 있는 경우만)
 *  3. 취소
 */
@Composable
fun PetPhotoOptionSheet(
    hasPhoto: Boolean,
    onDismiss: () -> Unit,
    onSelectFromAlbum: () -> Unit,
    onResetToDefault: () -> Unit,
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
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "프로필 사진 변경",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(35.dp))

            PhotoActionButton(
                text = "앨범에서 사진 선택",
                backgroundColor = colors.primary,
                textColor = colors.onPrimary,
                onClick = {
                    onDismiss()
                    onSelectFromAlbum()
                },
            )

            if (hasPhoto) {
                Spacer(modifier = Modifier.height(10.dp))
                PhotoActionButton(
                    text = "기본 이미지 적용",
                    backgroundColor = colors.cardSurface,
                    textColor = colors.primary,
                    onClick = {
                        onDismiss()
                        onResetToDefault()
                    },
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
private fun PhotoActionButton(
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

@Preview(showBackground = true)
@Composable
private fun PetPhotoOptionSheetPreview() {
    ForPetTheme {
        PetPhotoOptionSheet(
            hasPhoto = false,
            onDismiss = {},
            onSelectFromAlbum = {},
            onResetToDefault = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetPhotoOptionSheetWithPhotoPreview() {
    ForPetTheme {
        PetPhotoOptionSheet(
            hasPhoto = true,
            onDismiss = {},
            onSelectFromAlbum = {},
            onResetToDefault = {},
        )
    }
}
