package com.c4.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.c4.calculator.data.settings.ThemeMode
import com.c4.calculator.ui.navigation.AppNav
import com.c4.calculator.ui.theme.CalculatorTheme
import com.c4.calculator.ui.viewmodel.CalculatorViewModel
import com.c4.calculator.ui.viewmodel.HistoryViewModel
import com.c4.calculator.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val settings by settingsVm.settings.collectAsState()
            val dark = when (settings.theme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            CalculatorTheme(darkTheme = dark) {
                val calcVm: CalculatorViewModel = viewModel()
                val histVm: HistoryViewModel = viewModel()
                AppNav(calcVm, histVm, settingsVm)
            }
        }
    }
}
