package com.careflow.app.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CarePlan : Screen("care_plan")
    object Recovery : Screen("recovery")
    object Profile : Screen("profile")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
