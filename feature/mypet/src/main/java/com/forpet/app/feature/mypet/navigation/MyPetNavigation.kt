package com.forpet.app.feature.mypet.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.forpet.app.core.navigation.Route
import com.forpet.app.feature.mypet.MyPetScreen
import com.forpet.app.feature.mypet.PetRegisterScreen
import com.forpet.app.feature.mypet.PhotoCropScreen

fun NavController.navigateToMyPet(navOptions: NavOptions? = null) {
    this.navigate(Route.MyPet, navOptions)
}

fun NavController.navigateToPetRegister(petId: Long = 0L, navOptions: NavOptions? = null) {
    this.navigate(Route.PetRegister(petId), navOptions)
}

fun NavController.navigateToPhotoCrop(uri: String, petId: Long, navOptions: NavOptions? = null) {
    this.navigate(Route.PhotoCrop(uri, petId), navOptions)
}

fun NavGraphBuilder.myPetScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToEdit: (petId: Long) -> Unit,
    onNavigateToPhotoCrop: (uri: String, petId: Long) -> Unit,
) {
    composable<Route.MyPet> { entry ->
        val croppedPhotoPath by entry.savedStateHandle
            .getStateFlow<String?>("croppedPhotoPath", null)
            .collectAsStateWithLifecycle()

        MyPetScreen(
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateToPhotoCrop = onNavigateToPhotoCrop,
            croppedPhotoPath = croppedPhotoPath,
            onCroppedPhotoConsumed = {
                entry.savedStateHandle.remove<String>("croppedPhotoPath")
            },
        )
    }
}

fun NavGraphBuilder.petRegisterScreen(
    onNavigateUp: () -> Unit,
    onNavigateToPhotoCrop: (uri: String, petId: Long) -> Unit,
) {
    composable<Route.PetRegister> { entry ->
        val croppedPhotoPath by entry.savedStateHandle
            .getStateFlow<String?>("croppedPhotoPath", null)
            .collectAsStateWithLifecycle()

        PetRegisterScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToPhotoCrop = onNavigateToPhotoCrop,
            croppedPhotoPath = croppedPhotoPath,
            onCroppedPhotoConsumed = {
                entry.savedStateHandle.remove<String>("croppedPhotoPath")
            },
        )
    }
}

fun NavGraphBuilder.photoCropScreen(
    onPhotoSaved: (String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable<Route.PhotoCrop> {
        PhotoCropScreen(
            onPhotoSaved = onPhotoSaved,
            onNavigateUp = onNavigateUp,
        )
    }
}
