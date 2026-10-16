package com.c4.calculator.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class CalcMode { STANDARD, SCIENTIFIC, PROGRAMMER }
enum class AngleMode { DEG, RAD }

data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val calcMode: CalcMode = CalcMode.STANDARD,
    val angleMode: AngleMode = AngleMode.DEG,
    val decimalPlaces: Int = 10,
    val haptic: Boolean = true,
    val sound: Boolean = false
)

class SettingsRepository(private val context: Context) {
    private val store = context.dataStore
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val MODE = stringPreferencesKey("mode")
        val ANGLE = stringPreferencesKey("angle")
        val DECIMALS = intPreferencesKey("decimals")
        val HAPTIC = booleanPreferencesKey("haptic")
        val SOUND = booleanPreferencesKey("sound")
    }

    val settings: Flow<AppSettings> = store.data.map { p ->
        AppSettings(
            theme = runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM),
            calcMode = runCatching { CalcMode.valueOf(p[Keys.MODE] ?: "STANDARD") }.getOrDefault(CalcMode.STANDARD),
            angleMode = runCatching { AngleMode.valueOf(p[Keys.ANGLE] ?: "DEG") }.getOrDefault(AngleMode.DEG),
            decimalPlaces = p[Keys.DECIMALS] ?: 10,
            haptic = p[Keys.HAPTIC] ?: true,
            sound = p[Keys.SOUND] ?: false
        )
    }

    suspend fun setTheme(v: ThemeMode) = store.edit { it[Keys.THEME] = v.name }
    suspend fun setCalcMode(v: CalcMode) = store.edit { it[Keys.MODE] = v.name }
    suspend fun setAngleMode(v: AngleMode) = store.edit { it[Keys.ANGLE] = v.name }
    suspend fun setDecimalPlaces(v: Int) = store.edit { it[Keys.DECIMALS] = v }
    suspend fun setHaptic(v: Boolean) = store.edit { it[Keys.HAPTIC] = v }
    suspend fun setSound(v: Boolean) = store.edit { it[Keys.SOUND] = v }
}
