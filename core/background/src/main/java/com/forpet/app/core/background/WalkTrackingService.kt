package com.forpet.app.core.background

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.forpet.app.core.data.repository.WalkRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ── 이동경로 수집 전체 흐름 ──────────────────────────────────────────────────
 *
 *  [FusedLocationProviderClient]
 *      │  GPS/Wi-Fi/셀 기지국을 융합해 위치 추정. 2~3초마다 콜백 발생.
 *      ▼
 *  [handleLocation()]
 *      │
 *      ├─ ① 스파이크 필터: 속도 > 10 m/s → 버림
 *      ├─ ② 거리 누적: ≥ 2m 이동 시 totalDistance 증가 (UI 표시용)
 *      └─ ③ DB 저장:   ≥ 5m 이동 시 walk_points INSERT (경로 그리기용)
 *
 *  [WalkServiceState — companion object StateFlow]
 *      HomeViewModel이 구독 → WalkUiState로 변환 → WalkSection UI 업데이트
 *
 * Foreground Service로 실행하는 이유:
 *   화면이 꺼지거나 앱이 백그라운드로 가면 OS가 일반 프로세스를 제한해 GPS 콜백이 끊긴다.
 *   Foreground Service는 알림을 띄우는 대신 프로세스가 유지되므로 GPS가 지속됨.
 */
@AndroidEntryPoint
class WalkTrackingService : Service() {

    companion object {
        const val ACTION_START = "com.forpet.app.WALK_START"
        const val ACTION_STOP = "com.forpet.app.WALK_STOP"
        const val EXTRA_SESSION_ID = "extra_session_id"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "walk_tracking"

        // 사람이 낼 수 있는 현실적 최대 속도: 36 km/h (= 10 m/s).
        // 이 속도를 초과하는 위치 변화는 GPS 스파이크로 간주해 버린다.
        private const val MAX_REALISTIC_SPEED_MS = 10f // m/s

        private val _state = MutableStateFlow<WalkServiceState?>(null)
        val state: StateFlow<WalkServiceState?> = _state.asStateFlow()
    }

    @Inject
    lateinit var walkRepository: WalkRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timerJob: Job? = null

    private var sessionId: Long = 0L
    private var lastLocation: Location? = null
    private var lastSavedLocation: Location? = null
    private var totalDistance: Float = 0f
    private var elapsedSeconds: Long = 0L

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                sessionId = intent.getLongExtra(EXTRA_SESSION_ID, 0L)
                if (sessionId == 0L) {
                    stopSelf()
                    return START_NOT_STICKY
                }
                totalDistance = 0f
                elapsedSeconds = 0L
                lastLocation = null
                lastSavedLocation = null
                _state.value = WalkServiceState(sessionId, 0L, 0f)
                startForeground(NOTIFICATION_ID, buildNotification())
                startTimer()
                startLocationStreaming()
            }
            ACTION_STOP -> stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationStreaming()
        timerJob?.cancel()
        serviceScope.cancel()
        _state.value = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (true) {
                delay(1_000)
                elapsedSeconds++
                _state.value = WalkServiceState(sessionId, elapsedSeconds, totalDistance)
                updateNotification()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationStreaming() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { handleLocation(it) }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback!!,
            Looper.getMainLooper(),
        )
    }

    private fun stopLocationStreaming() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
    }

    private fun handleLocation(location: Location) {
        val prev = lastLocation

        // ① 스파이크 필터
        if (prev != null) {
            val distanceM = prev.distanceTo(location)
            val elapsedMs = location.time - prev.time
            if (elapsedMs > 0) {
                val speedMs = distanceM / (elapsedMs / 1000f)
                if (speedMs > MAX_REALISTIC_SPEED_MS) return
            }
        }

        // ② 거리 누적 (UI 실시간 표시용, 2m 미만 노이즈 제외)
        if (prev != null) {
            val delta = prev.distanceTo(location)
            if (delta >= 2f) totalDistance += delta
        }
        lastLocation = location
        _state.value = WalkServiceState(sessionId, elapsedSeconds, totalDistance)

        // ③ DB 저장 (경로 그리기용, 5m 미만은 저장 안 함)
        val prevSaved = lastSavedLocation
        if (prevSaved == null || prevSaved.distanceTo(location) >= 5f) {
            lastSavedLocation = location
            serviceScope.launch {
                walkRepository.addPoint(sessionId, location.latitude, location.longitude)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "산책 추적",
            NotificationManager.IMPORTANCE_LOW,
        ).apply { description = "산책 중 GPS를 유지합니다" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val minutes = elapsedSeconds / 60
        val km = "%.1f".format(totalDistance / 1000f)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("산책 중")
            .setContentText("${minutes}분 · ${km}km")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification() {
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, buildNotification())
    }
}

/** 서비스가 노출하는 실시간 산책 상태 */
data class WalkServiceState(
    val sessionId: Long,
    val elapsedSeconds: Long,
    val distanceMeters: Float,
)
