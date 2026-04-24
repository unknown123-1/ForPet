package com.forpet.app.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.forpet.app.core.navigation.Route
import com.forpet.app.feature.home.HomeScreen

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(Route.Home, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onNavigateToWalkResult: (sessionId: Long) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToEdit: (petId: Long) -> Unit,
    bottomSheetContent: @Composable (isExpanded: Boolean, onToggleExpand: () -> Unit) -> Unit,
) {
    composable<Route.Home> {
        HomeScreen(
            onNavigateToWalkResult = onNavigateToWalkResult,
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToEdit = onNavigateToEdit,
            bottomSheetContent = bottomSheetContent,
        )
    }
}
