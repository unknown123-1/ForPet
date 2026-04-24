package com.forpet.app.feature.schedule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.forpet.app.core.navigation.Route
import com.forpet.app.feature.schedule.AddScheduleScreen
import com.forpet.app.feature.schedule.EditScheduleScreen

fun NavController.navigateToAddSchedule(selectedDate: String? = null, navOptions: NavOptions? = null) {
    this.navigate(Route.AddSchedule(selectedDate), navOptions)
}

fun NavController.navigateToEditSchedule(scheduleId: String, navOptions: NavOptions? = null) {
    this.navigate(Route.EditSchedule(scheduleId), navOptions)
}

fun NavGraphBuilder.addScheduleScreen(
    onNavigateUp: () -> Unit,
) {
    composable<Route.AddSchedule> {
        AddScheduleScreen(onNavigateUp = onNavigateUp)
    }
}

fun NavGraphBuilder.editScheduleScreen(
    onNavigateUp: () -> Unit,
) {
    composable<Route.EditSchedule> {
        EditScheduleScreen(onNavigateUp = onNavigateUp)
    }
}
