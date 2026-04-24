package com.forpet.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForPetTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    val colors = LocalForPetColors.current
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false,
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimeInput(
                state = state,
                colors = TimePickerDefaults.colors(
                    timeSelectorSelectedContainerColor = colors.cardSurface,
                    timeSelectorSelectedContentColor = colors.textPrimary,
                    timeSelectorUnselectedContainerColor = colors.cardSurface,
                    timeSelectorUnselectedContentColor = colors.textPrimary,
                    periodSelectorSelectedContainerColor = colors.primary,
                    periodSelectorSelectedContentColor = colors.onPrimary,
                    periodSelectorUnselectedContainerColor = colors.cardSurface,
                    periodSelectorUnselectedContentColor = colors.textPrimary,
                ),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("취소", color = colors.textPrimary)
                }
                TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                    Text("확인", color = colors.primary)
                }
            }
        }
    }
}
