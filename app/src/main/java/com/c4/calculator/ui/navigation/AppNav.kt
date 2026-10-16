package com.c4.calculator.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.c4.calculator.ui.screens.CalculatorScreen
import com.c4.calculator.ui.screens.ConverterScreen
import com.c4.calculator.ui.screens.HistoryScreen
import com.c4.calculator.ui.screens.SettingsScreen
import com.c4.calculator.ui.viewmodel.CalculatorViewModel
import com.c4.calculator.ui.viewmodel.HistoryViewModel
import com.c4.calculator.ui.viewmodel.SettingsViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Calc : Screen("calc", "Calc", Icons.Filled.Calculate)
    data object Converter : Screen("converter", "Convert", Icons.Filled.SwapHoriz)
    data object History : Screen("history", "History", Icons.Filled.History)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun AppNav(
    calcVm: CalculatorViewModel,
    histVm: HistoryViewModel,
    settingsVm: SettingsViewModel
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Calc, Screen.Converter, Screen.History, Screen.Settings)
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.Calc.route) {
            composable(Screen.Calc.route) { CalculatorScreen(calcVm, settingsVm, padding) }
            composable(Screen.Converter.route) { ConverterScreen(padding) }
            composable(Screen.History.route) { HistoryScreen(histVm, calcVm, padding) }
            composable(Screen.Settings.route) { SettingsScreen(settingsVm, padding) }
        }
    }
}
