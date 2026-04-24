package com.forpet.app.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.forpet.app.core.designsystem.theme.LocalForPetColors

/**
 * 공용 텍스트 필드. 아이콘+레이블 헤더와 포커스 애니메이션이 있는 밑줄 스타일.
 * icon이 null이면 레이블만 표시.
 */
@Composable
fun ForPetTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val colors = LocalForPetColors.current
    val resolvedIconTint = iconTint ?: colors.primary
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val underlineColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.textSecondary
            isFocused -> colors.primary
            else -> colors.textSecondary
        },
        animationSpec = tween(durationMillis = 200),
        label = "underlineColor",
    )

    Column(modifier = modifier) {
        if (icon != null) {
            ForPetIconLabel(icon = icon, iconTint = resolvedIconTint, label = label)
        } else {
            Text(
                text = label,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = colors.textPrimary,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Medium,
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        ),
                        interactionSource = interactionSource,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                if (trailingContent != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingContent()
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isFocused) 2.dp else 1.dp)
                    .background(underlineColor),
            )
        }
    }
}

/**
 * 텍스트 필드 우측의 X 클리어 버튼
 */
@Composable
fun ForPetClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalForPetColors.current
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(colors.inactive)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "삭제",
            tint = colors.onPrimary,
            modifier = Modifier.size(12.dp),
        )
    }
}
