package com.c4.calculator

import android.app.Application
import com.c4.calculator.data.history.AppDatabase
import com.c4.calculator.data.settings.SettingsRepository

class CalculatorApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val settings by lazy { SettingsRepository(this) }
}
