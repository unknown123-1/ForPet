package com.forpet.app.core.ui

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.forpet.app.core.designsystem.theme.LocalForPetColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForPetDatePickerDialog(
    initialDateMillis: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val colors = LocalForPetColors.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onConfirm(it) }
                onDismiss()
            }) {
                Text("확인", color = colors.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = colors.textPrimary)
            }
        },
        colors = DatePickerDefaults.colors(containerColor = colors.surface),
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = colors.surface,
                titleContentColor = colors.textPrimary,
                headlineContentColor = colors.textPrimary,
                weekdayContentColor = colors.textPrimary,
                subheadContentColor = colors.textPrimary,
                navigationContentColor = colors.textPrimary,
                yearContentColor = colors.textPrimary,
                currentYearContentColor = colors.primary,
                selectedYearContainerColor = colors.primary,
                selectedYearContentColor = colors.onPrimary,
                dayContentColor = colors.textPrimary,
                selectedDayContainerColor = colors.primary,
                selectedDayContentColor = colors.onPrimary,
                todayContentColor = colors.primary,
                todayDateBorderColor = colors.primary,
                dayInSelectionRangeContainerColor = colors.primaryContainer,
                dayInSelectionRangeContentColor = colors.onPrimaryContainer,
                dividerColor = colors.background,
            ),
        )
    }
}
