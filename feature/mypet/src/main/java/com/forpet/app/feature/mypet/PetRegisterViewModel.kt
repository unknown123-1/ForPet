package com.forpet.app.feature.mypet

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.forpet.app.core.data.repository.PetRepository
import com.forpet.app.core.model.Pet
import com.forpet.app.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PetRegisterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val petRepository: PetRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val route: Route.PetRegister = savedStateHandle.toRoute()
    private val editingPetId: Long = route.petId

    private val _formState = MutableStateFlow(PetFormState(id = editingPetId))
    val formState: StateFlow<PetFormState> = _formState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        if (editingPetId != 0L) {
            viewModelScope.launch {
                petRepository.observeFirstPet().collect { pet ->
                    if (pet != null && pet.id == editingPetId) {
                        _formState.value = PetFormState(
                            id = pet.id,
                            name = pet.name,
                            birthday = pet.birthday,
                            firstMetDay = pet.firstMetDay,
                            walkGoalKm = pet.walkGoalKm,
                            photoUri = pet.photoUri,
                        )
                    }
                }
            }
        }
    }

    fun updateName(name: String) = _formState.update { it.copy(name = name.take(6)) }
    fun updateBirthday(date: LocalDate?) = _formState.update { it.copy(birthday = date) }
    fun updateFirstMetDay(date: LocalDate?) = _formState.update { it.copy(firstMetDay = date) }
    fun updateWalkGoal(km: Int) = _formState.update { it.copy(walkGoalKm = km) }
    fun updatePhotoUri(uri: String?) = _formState.update { it.copy(photoUri = uri) }

    fun resetPhotoUri() {
        val currentUri = _formState.value.photoUri ?: return
        viewModelScope.launch(Dispatchers.IO) {
            File(currentUri).delete()
        }
        _formState.update { it.copy(photoUri = null) }
    }

    fun save() {
        val state = _formState.value
        if (state.name.isBlank()) return
        viewModelScope.launch {
            val pet = Pet(
                id = state.id,
                name = state.name,
                birthday = state.birthday,
                firstMetDay = state.firstMetDay,
                photoUri = state.photoUri,
                walkGoalKm = state.walkGoalKm,
            )
            val savedId = petRepository.savePet(pet)

            if (state.id == 0L && state.photoUri != null) {
                withContext(Dispatchers.IO) {
                    val tempFile = File(context.filesDir, "pet_temp_photo.jpg")
                    if (tempFile.exists() && tempFile.absolutePath == state.photoUri) {
                        val finalFile = File(context.filesDir, "pet_${savedId}_photo.jpg")
                        if (tempFile.renameTo(finalFile)) {
                            petRepository.savePet(
                                pet.copy(id = savedId, photoUri = finalFile.absolutePath)
                            )
                        }
                    }
                }
            }

            _isSaved.value = true
        }
    }
}

data class PetFormState(
    val id: Long = 0,
    val name: String = "",
    val birthday: LocalDate? = null,
    val firstMetDay: LocalDate? = null,
    val walkGoalKm: Int = 3,
    val photoUri: String? = null,
)
