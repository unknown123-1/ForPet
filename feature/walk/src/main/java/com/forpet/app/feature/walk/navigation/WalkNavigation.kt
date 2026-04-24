package com.forpet.app.feature.walk.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.forpet.app.core.navigation.Route
import com.forpet.app.feature.walk.WalkResultScreen

fun NavController.navigateToWalkResult(sessionId: Long, navOptions: NavOptions? = null) {
    this.navigate(Route.WalkResult(sessionId), navOptions)
}

fun NavGraphBuilder.walkResultScreen(
    onDone: () -> Unit,
) {
    composable<Route.WalkResult> {
        WalkResultScreen(onDone = onDone)
    }
}
