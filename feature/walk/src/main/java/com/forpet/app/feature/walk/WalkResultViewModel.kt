package com.forpet.app.feature.walk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.forpet.app.core.data.repository.WalkRepository
import com.forpet.app.core.model.WalkPoint
import com.forpet.app.core.model.WalkSession
import com.forpet.app.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WalkResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    walkRepository: WalkRepository,
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.toRoute<Route.WalkResult>().sessionId

    val uiState: StateFlow<WalkResultUiState> = combine(
        walkRepository.observeSession(sessionId),
        walkRepository.observeSessionPoints(sessionId),
    ) { session, points ->
        if (session != null) {
            WalkResultUiState.Success(session = session, points = points)
        } else {
            WalkResultUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WalkResultUiState.Loading,
    )
}

sealed interface WalkResultUiState {
    data object Loading : WalkResultUiState
    data class Success(
        val session: WalkSession,
        val points: List<WalkPoint>,
    ) : WalkResultUiState
}
