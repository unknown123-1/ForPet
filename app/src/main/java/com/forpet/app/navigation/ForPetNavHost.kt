package com.forpet.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.forpet.app.core.navigation.Route
import com.forpet.app.feature.calendar.CalendarScreen
import com.forpet.app.feature.home.navigation.homeScreen
import com.forpet.app.feature.mypet.navigation.myPetScreen
import com.forpet.app.feature.mypet.navigation.navigateToPhotoCrop
import com.forpet.app.feature.mypet.navigation.navigateToPetRegister
import com.forpet.app.feature.mypet.navigation.petRegisterScreen
import com.forpet.app.feature.mypet.navigation.photoCropScreen
import com.forpet.app.feature.schedule.navigation.addScheduleScreen
import com.forpet.app.feature.schedule.navigation.editScheduleScreen
import com.forpet.app.feature.schedule.navigation.navigateToEditSchedule
import com.forpet.app.feature.walk.navigation.navigateToWalkResult
import com.forpet.app.feature.walk.navigation.walkResultScreen

@Composable
fun ForPetNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier,
    ) {
        // Home (CalendarScreen은 app에서 조립하여 주입)
        homeScreen(
            onNavigateToWalkResult = { sessionId -> navController.navigateToWalkResult(sessionId) },
            onNavigateToRegister = { navController.navigateToPetRegister() },
            onNavigateToEdit = { petId -> navController.navigateToPetRegister(petId) },
            bottomSheetContent = { isExpanded, onToggleExpand ->
                CalendarScreen(
                    isExpanded = isExpanded,
                    onToggleExpand = onToggleExpand,
                    onEditSchedule = { scheduleId ->
                        navController.navigateToEditSchedule(scheduleId.toString())
                    },
                    onNavigateToWalkResult = { sessionId ->
                        navController.navigateToWalkResult(sessionId)
                    },
                )
            },
        )

        // My Pet
        myPetScreen(
            onNavigateToRegister = { navController.navigateToPetRegister() },
            onNavigateToEdit = { petId -> navController.navigateToPetRegister(petId) },
            onNavigateToPhotoCrop = { uri, petId -> navController.navigateToPhotoCrop(uri, petId) },
        )

        // Schedule
        addScheduleScreen(onNavigateUp = { navController.navigateUp() })
        editScheduleScreen(onNavigateUp = { navController.navigateUp() })

        // Pet Register
        petRegisterScreen(
            onNavigateUp = { navController.navigateUp() },
            onNavigateToPhotoCrop = { uri, petId -> navController.navigateToPhotoCrop(uri, petId) },
        )

        // Walk Result
        walkResultScreen(
            onDone = { navController.popBackStack(Route.Home, inclusive = false) },
        )

        // Photo Crop
        photoCropScreen(
            onPhotoSaved = { filePath ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("croppedPhotoPath", filePath)
                navController.navigateUp()
            },
            onNavigateUp = { navController.navigateUp() },
        )
    }
}
