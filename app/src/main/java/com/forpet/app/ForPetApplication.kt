package com.forpet.app

import android.app.Application
import com.forpet.app.core.background.AbandonedSessionCleaner
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ForPetApplication : Application() {

    @Inject
    lateinit var abandonedSessionCleaner: AbandonedSessionCleaner

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // 앱 시작 시 미완료 산책 세션 정리
        // HomeViewModel.init보다 앞서 실행되므로 어떤 화면으로 진입해도 항상 처리됨
        applicationScope.launch {
            abandonedSessionCleaner.cleanUpIfNeeded()
        }
    }
}
