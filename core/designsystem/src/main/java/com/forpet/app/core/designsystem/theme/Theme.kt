package com.forpet.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple80,
    onPrimary = White,
    background = Gray50,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = ForPetBlack50,
    primaryContainer = LightPurple,
    onPrimaryContainer = Purple80,
    outline = Gray300,
    outlineVariant = Gray300,
    inverseSurface = Gray900,
    inverseOnSurface = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = White,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = DarkText.copy(alpha = 0.65f),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    outline = DarkInactive,
    outlineVariant = DarkInactive,
    inverseSurface = DarkNavBackground,
    inverseOnSurface = DarkText,
)

@Composable
fun ForPetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val forPetColors = if (darkTheme) darkForPetColors else lightForPetColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalForPetColors provides forPetColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
