package com.forpet.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forpet.app.core.designsystem.theme.ForPetColors
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors

/**
 * 바텀 네비게이션 바의 탭 아이템 정의
 */
data class BottomBarTab(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String = label,
)

/**
 * 파라미터화된 바텀 네비게이션 바
 *
 * @param tabs 좌/우 탭 목록 (현재 2개 고정 레이아웃)
 * @param selectedIndex 현재 선택된 탭 인덱스
 * @param onTabClick 탭 클릭 콜백
 * @param onCenterClick 중앙 FAB(+) 클릭 콜백
 */
@Composable
fun ForPetBottomBar(
    tabs: List<BottomBarTab>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
    onCenterClick: () -> Unit,
) {
    val colors = LocalForPetColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp, top = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            color = colors.navBackground,
            shadowElevation = 8.dp,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                if (tabs.isNotEmpty()) {
                    BottomBarTabButton(
                        tab = tabs[0],
                        isSelected = selectedIndex == 0,
                        onClick = { onTabClick(0) },
                        colors = colors,
                    )
                }

                IconButton(
                    onClick = onCenterClick,
                    modifier = Modifier.size(45.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                if (tabs.size > 1) {
                    BottomBarTabButton(
                        tab = tabs[1],
                        isSelected = selectedIndex == 1,
                        onClick = { onTabClick(1) },
                        colors = colors,
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarTabButton(
    tab: BottomBarTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: ForPetColors,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .width(130.dp)
            .height(48.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (isSelected) colors.navSelectedBg else Color.Transparent,
        ),
        shape = RoundedCornerShape(24.dp),
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.contentDescription,
            tint = if (isSelected) colors.navSelectedContent else colors.navUnselectedContent,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = tab.label,
            color = if (isSelected) colors.navSelectedContent else colors.navUnselectedContent,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForPetBottomBarPreview() {
    ForPetTheme {
        ForPetBottomBar(
            tabs = listOf(
                BottomBarTab(Icons.Default.CalendarToday, "오늘 할 일"),
                BottomBarTab(Icons.Default.Pets, "나의 펫"),
            ),
            selectedIndex = 0,
            onTabClick = {},
            onCenterClick = {},
        )
    }
}
