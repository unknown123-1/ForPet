package com.forpet.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Brand ─────────────────────────────────────────────────────────────────────
val Purple80 = Color(0xFF6C57F2) // 메인 보라색 (Figma 기준)
val Purple90 = Color(0xFF8E5BE8) // 보라 그라디언트 끝 색
val ForPetPurple = Purple80
val LightPurple = Color(0xFFF0EFFF) // 연한 보라 (불투명)

// ── Neutrals ──────────────────────────────────────────────────────────────────
val White = Color(0xFFFFFFFF)
val Gray50 = Color(0xFFF6F6F8)   // 배경색
val Gray300 = Color(0xFFDBDBE3)  // 비활성 아이콘, 핸들, 과거 날짜 배경 등
val Gray900 = Color(0xFF252525)  // 다크 그레이 (바텀바 배경 등)
val ForPetBlack = Gray900
val ForPetBlack50 = ForPetBlack.copy(alpha = 0.65f)
val Black = Color(0xFF000000)

// ── Schedule Type ─────────────────────────────────────────────────────────────
val Orange = Color(0xFFFF9C39)
val Red    = Color(0xFFFF5E5E)
val Green  = Color(0xFF34C29C)
val Blue   = Color(0xFF4EA8F7)

// ── Dark Mode Raw Colors ─────────────────────────────────────────────────────
//
// Material / HIG 기준에 맞춰 다크에서는 상위 레이어일수록 더 밝게 보이도록 설계한다.
// 라이트 슬롯과의 시맨틱 매핑은 아래와 같다.
//
//   • Gray50  -> DarkBackground / DarkCardSurface 계열의 중립 다크
//   • White   -> DarkSurface 계열의 상위 레이어
//   • Gray300 -> DarkInactive 계열의 outline / disabled
//
// 실제 사용에서는 같은 라이트 회색이라도 역할이 다르면 다크에서 레이어 밝기를 분리한다.
// (배경 < 시트/폼 < 카드/네비 pill)
internal val DarkBackground         = Color(0xFF11131A)
internal val DarkSurface            = Color(0xFF1A1D27)
internal val DarkCardSurface        = Color(0xFF252938)
internal val DarkText               = Color(0xFFF1F3F8)
internal val DarkInactive           = Color(0xFF4B5163)
internal val DarkPrimary           = Purple80           // 브랜드 보라 — 라이트와 동일
internal val DarkPrimaryGradient   = Purple90           // 그라디언트 — 라이트와 동일
internal val DarkPrimaryContainer   = Color(0xFF312C55)
internal val DarkOnPrimaryContainer = Color(0xFFF3EEFF)
internal val DarkNavBackground      = Color(0xFF2B3041)
internal val DarkNavSelectedBg      = Color(0xFF3A4054)

// ── Semantic Color Scheme ─────────────────────────────────────────────────────

/**
 * ForPet 앱의 시맨틱 색상 슬롯.
 * 라이트/다크 모드 모두 이 클래스를 통해 색상에 접근합니다.
 *
 * 슬롯 의미:
 *  - background      : 화면/Scaffold 배경 (Gray50 계열)
 *  - surface         : 시트, 다이얼로그, White Scaffold 배경; 반전된 배경 위의 텍스트 색
 *  - cardSurface     : 카드·칩·버튼 배경 (Gray50 계열 아이템)
 *  - textPrimary     : 주요 텍스트; 선택된 날짜 배경(반전)
 *  - textSecondary   : 보조·힌트 텍스트 (ForPetBlack50 계열)
 *  - inactive        : 테두리, 구분선, 비활성 배경 (Gray300 계열)
 *  - primary         : 브랜드 보라 (Purple80 계열)
 *  - primaryGradient : 그라디언트 끝 색 (Purple90 계열)
 *  - onPrimary       : primary 위의 텍스트/아이콘 (항상 밝은 색)
 *  - primaryContainer: 연한 보라 배경 (LightPurple 계열)
 *  - onPrimaryContainer: primaryContainer 위의 텍스트/아이콘
 *  - navBackground   : 바텀 네비게이션 바 배경
 *  - navSelectedBg   : 선택된 탭 배경
 *  - navSelectedContent : 선택된 탭 아이콘/텍스트
 *  - navUnselectedContent : 미선택 탭 아이콘/텍스트
 */
@Immutable
data class ForPetColors(
    val background: Color,
    val surface: Color,
    val cardSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val inactive: Color,
    val primary: Color,
    val primaryGradient: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val navBackground: Color,
    val navSelectedBg: Color,
    val navSelectedContent: Color,
    val navUnselectedContent: Color,
    val isDark: Boolean,
)

val lightForPetColors = ForPetColors(
    background           = Gray50,
    surface              = White,
    cardSurface          = Gray50,
    textPrimary          = Gray900,
    textSecondary        = ForPetBlack50,
    inactive             = Gray300,
    primary              = Purple80,
    primaryGradient      = Purple90,
    onPrimary            = White,
    primaryContainer     = LightPurple,
    onPrimaryContainer   = Purple80,
    navBackground        = Gray900,
    navSelectedBg        = White,
    navSelectedContent   = Gray900,
    navUnselectedContent = White.copy(alpha = 0.4f),
    isDark               = false,
)

val darkForPetColors = ForPetColors(
    background           = DarkBackground,
    surface              = DarkSurface,
    cardSurface          = DarkCardSurface,
    textPrimary          = DarkText,
    textSecondary        = DarkText.copy(alpha = 0.72f),
    inactive             = DarkInactive,
    primary              = DarkPrimary,
    primaryGradient      = DarkPrimaryGradient,
    onPrimary            = White,
    primaryContainer     = DarkPrimaryContainer,
    onPrimaryContainer   = DarkOnPrimaryContainer,
    navBackground        = DarkNavBackground,
    navSelectedBg        = DarkNavSelectedBg,
    navSelectedContent   = DarkText,
    navUnselectedContent = DarkText.copy(alpha = 0.62f),
    isDark               = true,
)

val LocalForPetColors = staticCompositionLocalOf { lightForPetColors }
