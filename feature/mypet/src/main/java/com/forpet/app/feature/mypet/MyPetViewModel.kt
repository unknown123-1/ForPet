package com.forpet.app.feature.mypet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forpet.app.core.data.repository.PetRepository
import com.forpet.app.core.data.repository.WalkRepository
import com.forpet.app.core.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyPetViewModel @Inject constructor(
    private val petRepository: PetRepository,
    walkRepository: WalkRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val uiState: StateFlow<MyPetUiState> = combine(
        petRepository.observeFirstPet(),
        walkRepository.observeAllCompletedSessions(),
    ) { pet, allSessions ->
        if (pet == null) {
            MyPetUiState.NoPet
        } else {
            MyPetUiState.HasPet(
                pet = pet,
                totalDistanceMeters = allSessions.sumOf { it.distanceMeters.toDouble() }.toFloat(),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MyPetUiState.Loading,
    )

    fun updatePhoto(uri: String) {
        viewModelScope.launch {
            val pet = petRepository.observeFirstPet().first() ?: return@launch
            petRepository.savePet(pet.copy(photoUri = uri))
        }
    }

    fun resetPhoto() {
        viewModelScope.launch {
            val pet = petRepository.observeFirstPet().first() ?: return@launch
            withContext(Dispatchers.IO) {
                File(context.filesDir, "pet_${pet.id}_photo.jpg").delete()
            }
            petRepository.savePet(pet.copy(photoUri = null))
        }
    }
}

sealed interface MyPetUiState {
    data object Loading : MyPetUiState
    data object NoPet : MyPetUiState
    data class HasPet(
        val pet: Pet,
        val totalDistanceMeters: Float,
    ) : MyPetUiState
}
