package com.forpet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.ui.ForPetApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-Edge 활성화 (상태바/네비바 투명 처리 및 컨텐츠 확장)
        enableEdgeToEdge()
        
        setContent {
            ForPetTheme {
                ForPetApp()
            }
        }
    }
}
