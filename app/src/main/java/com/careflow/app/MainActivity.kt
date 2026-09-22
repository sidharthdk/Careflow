package com.careflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.careflow.app.core.navigation.BottomNavItem
import com.careflow.app.core.navigation.Screen
import com.careflow.app.core.ui.theme.CareFlowTheme
import com.careflow.app.feature.careplan.CarePlanScreen
import com.careflow.app.feature.home.HomeScreen
import com.careflow.app.feature.profile.ProfileScreen
import com.careflow.app.feature.recovery.RecoveryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareFlowTheme {
                CareFlowApp()
            }
        }
    }
}

@Composable
fun CareFlowApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Default.Home),
        BottomNavItem(Screen.CarePlan, "Care Plan", Icons.Default.List),
        BottomNavItem(Screen.Recovery, "Recovery", Icons.Default.Favorite),
        BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.CarePlan.route) {
                CarePlanScreen()
            }
            composable(Screen.Recovery.route) {
                RecoveryScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
