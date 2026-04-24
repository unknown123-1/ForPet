package com.forpet.app.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forpet.app.core.designsystem.component.BottomBarTab
import com.forpet.app.core.designsystem.component.ForPetBottomBar
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.navigation.Route
import com.forpet.app.feature.schedule.navigation.navigateToAddSchedule
import com.forpet.app.navigation.ForPetNavHost

/**
 * 앱의 Top-Level 탭 목적지 정의
 *
 * 바텀 바에 표시될 탭의 아이콘, 라벨, 라우트를 한 곳에서 관리한다.
 */
enum class TopLevelDestination(
    val icon: ImageVector,
    val label: String,
    val route: Route,
) {
    TODAY(
        icon = Icons.Default.CalendarToday,
        label = "오늘 할 일",
        route = Route.Home,
    ),
    MY_PET(
        icon = Icons.Default.Pets,
        label = "나의 펫",
        route = Route.MyPet,
    ),
}

@Composable
fun ForPetApp(
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val destinations = TopLevelDestination.entries

    // 바텀 바 표시 여부 결정 (Top Level 화면일 때만 표시)
    val showBottomBar = remember(currentDestination) {
        destinations.any { currentDestination?.hasRoute(it.route::class) == true }
    }
    val forPetColors = LocalForPetColors.current

    // 탭 선택 상태를 저장 (기본값 0: TODAY)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // 목적지가 변경될 때마다 탭 상태 업데이트
    LaunchedEffect(currentDestination) {
        destinations.forEachIndexed { index, dest ->
            if (currentDestination?.hasRoute(dest.route::class) == true) {
                selectedTab = index
            }
        }
    }

    // 바텀 바에 전달할 탭 목록
    val bottomBarTabs = remember {
        destinations.map { BottomBarTab(icon = it.icon, label = it.label) }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ForPetBottomBar(
                    tabs = bottomBarTabs,
                    selectedIndex = selectedTab,
                    onTabClick = { index ->
                        navController.navigateToTopLevel(destinations[index].route)
                    },
                    onCenterClick = {
                        navController.navigateToAddSchedule()
                    },
                )
            }
        },
        containerColor = forPetColors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        ForPetNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/** Top Level 화면으로 이동하는 확장 함수 */
private fun NavHostController.navigateToTopLevel(route: Route) {
    this.navigate(route) {
        popUpTo(this@navigateToTopLevel.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
