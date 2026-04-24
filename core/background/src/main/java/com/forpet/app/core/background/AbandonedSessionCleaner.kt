package com.forpet.app.core.background

import android.location.Location
import com.forpet.app.core.data.repository.WalkRepository
import com.forpet.app.core.model.WalkPoint
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 앱 재시작 시 미완료 산책 세션을 정리한다.
 *
 * 미완료 세션이 생기는 경우:
 *   - OS가 메모리 부족으로 프로세스를 강제 종료
 *   - 폰 재부팅
 *   - 사용자가 설정에서 앱 강제 종료
 *
 * (사용자가 태스크에서 앱을 스와이프하는 경우는 Foreground Service가 살아있어 해당 없음)
 *
 * 정리 전략:
 *   포인트 없음 → 세션 삭제 (의미 없는 데이터)
 *   포인트 있음 → 마지막 포인트 기준으로 완료 처리 (기록 보존)
 */
@Singleton
class AbandonedSessionCleaner @Inject constructor(
    private val walkRepository: WalkRepository,
) {
    suspend fun cleanUpIfNeeded() {
        // 서비스가 현재 실행 중이면 정상 산책 중이므로 건너뜀
        if (WalkTrackingService.state.value != null) return

        val abandoned = walkRepository.getAbandonedSession() ?: return

        val points = walkRepository.getSessionPointsOnce(abandoned.id)

        if (points.isEmpty()) {
            // 포인트가 하나도 없으면 산책이 시작되지 않은 것 → 세션 삭제
            walkRepository.deleteSession(abandoned.id)
        } else {
            // 포인트가 있으면 경로 데이터를 살려서 완료 처리
            val distanceM = calculateTotalDistance(points)
            val durationSec = (points.last().recordedAt - abandoned.startedAt) / 1000L
            val avgSpeedKmh = if (durationSec > 0) {
                (distanceM / 1000f) / (durationSec / 3600f)
            } else 0f

            walkRepository.finishSession(
                sessionId = abandoned.id,
                distanceMeters = distanceM,
                avgSpeedKmh = avgSpeedKmh,
                durationSeconds = durationSec,
            )
        }
    }

    /** 포인트 목록의 총 이동 거리를 계산한다 (미터). */
    private fun calculateTotalDistance(points: List<WalkPoint>): Float {
        val results = FloatArray(1)
        var total = 0f
        for (i in 1 until points.size) {
            Location.distanceBetween(
                points[i - 1].latitude, points[i - 1].longitude,
                points[i].latitude, points[i].longitude,
                results,
            )
            total += results[0]
        }
        return total
    }
}
