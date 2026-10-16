package com.c4.calculator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.c4.calculator.data.settings.AppSettings
import com.c4.calculator.data.settings.SettingsRepository
import com.c4.calculator.data.settings.ThemeMode
import com.c4.calculator.data.settings.CalcMode
import com.c4.calculator.data.settings.AngleMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as com.c4.calculator.CalculatorApp).settings
    val settings: StateFlow<AppSettings> = repo.settings.stateIn(
        viewModelScope, SharingStarted.Eagerly, AppSettings()
    )
    fun setTheme(v: ThemeMode) = viewModelScope.launch { repo.setTheme(v) }
    fun setCalcMode(v: CalcMode) = viewModelScope.launch { repo.setCalcMode(v) }
    fun setAngleMode(v: AngleMode) = viewModelScope.launch { repo.setAngleMode(v) }
    fun setDecimalPlaces(v: Int) = viewModelScope.launch { repo.setDecimalPlaces(v) }
    fun setHaptic(v: Boolean) = viewModelScope.launch { repo.setHaptic(v) }
    fun setSound(v: Boolean) = viewModelScope.launch { repo.setSound(v) }
    fun clearHistory(app: Application) = viewModelScope.launch {
        com.c4.calculator.data.history.AppDatabase.getDatabase(app).historyDao().clearAll()
    }
}
