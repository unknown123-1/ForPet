package com.forpet.app.feature.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forpet.app.core.background.WalkTrackingService
import com.forpet.app.core.data.repository.PetRepository
import com.forpet.app.core.data.repository.WalkRepository
import com.forpet.app.core.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val walkRepository: WalkRepository,
    petRepository: PetRepository,
) : ViewModel() {

    /** WalkTrackingService 상태를 UI 상태로 변환 */
    val walkUiState: StateFlow<WalkUiState> = WalkTrackingService.state
        .map { serviceState ->
            if (serviceState != null) {
                WalkUiState(
                    isRunning = true,
                    sessionId = serviceState.sessionId,
                    elapsedSeconds = serviceState.elapsedSeconds,
                    distanceMeters = serviceState.distanceMeters,
                )
            } else {
                WalkUiState()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalkUiState(),
        )

    /** 오늘 완료된 산책의 합산 통계 (홈 화면 상단 표시용) */
    val todayStats: StateFlow<TodayWalkStats?> = walkRepository
        .observeSessionsByDate(LocalDate.now())
        .map { sessions ->
            if (sessions.isEmpty()) null
            else TodayWalkStats(
                totalDistanceMeters = sessions.sumOf { it.distanceMeters.toDouble() }.toFloat(),
                totalSeconds = sessions.sumOf { it.durationSeconds },
                lastEndedAt = sessions.maxOfOrNull { it.endedAt ?: it.startedAt },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    /** 현재 등록된 펫 정보 (홈 화면 프로필 섹션 표시용) */
    val pet: StateFlow<Pet?> = petRepository
        .observeFirstPet()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    /** 산책 완료 후 WalkResult 화면으로 이동하기 위한 이벤트 */
    private val _navigationEvent = MutableSharedFlow<Long>()
    val navigationEvent: SharedFlow<Long> = _navigationEvent.asSharedFlow()

    fun startWalk() {
        if (WalkTrackingService.state.value != null) return
        viewModelScope.launch {
            val sessionId = walkRepository.startSession()
            val intent = Intent(context, WalkTrackingService::class.java).apply {
                action = WalkTrackingService.ACTION_START
                putExtra(WalkTrackingService.EXTRA_SESSION_ID, sessionId)
            }
            context.startForegroundService(intent)
        }
    }

    fun stopWalk() {
        val serviceState = WalkTrackingService.state.value ?: return
        val sessionId = serviceState.sessionId
        val distMeters = serviceState.distanceMeters
        val elapsedSec = serviceState.elapsedSeconds
        val avgSpeed = if (elapsedSec > 0) (distMeters / 1000f) / (elapsedSec / 3600f) else 0f

        context.stopService(Intent(context, WalkTrackingService::class.java))

        viewModelScope.launch {
            walkRepository.finishSession(
                sessionId = sessionId,
                distanceMeters = distMeters,
                avgSpeedKmh = avgSpeed,
                durationSeconds = elapsedSec,
            )
            _navigationEvent.emit(sessionId)
        }
    }
}

data class WalkUiState(
    val isRunning: Boolean = false,
    val sessionId: Long = 0L,
    val elapsedSeconds: Long = 0L,
    val distanceMeters: Float = 0f,
)

/** 오늘 완료된 산책의 합산 통계 */
data class TodayWalkStats(
    val totalDistanceMeters: Float,
    val totalSeconds: Long,
    val lastEndedAt: Long?,
)
