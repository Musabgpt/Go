package com.c4.calculator

import android.app.Application

class CalculatorApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val settings by lazy { SettingsRepository(this) }
}
