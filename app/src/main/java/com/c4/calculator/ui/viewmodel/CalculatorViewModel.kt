package com.c4.calculator.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.c4.calculator.data.history.AppDatabase
import com.c4.calculator.data.history.HistoryEntity
import com.c4.calculator.data.settings.AppSettings
import com.c4.calculator.data.settings.SettingsRepository
import com.c4.calculator.engine.AngleMode as EngineAngleMode
import com.c4.calculator.engine.ExpressionParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val settingsRepo = (app as com.c4.calculator.CalculatorApp).settings
    val settings: StateFlow<AppSettings> = settingsRepo.settings.stateIn(
        viewModelScope, SharingStarted.Eagerly, AppSettings()
    )

    var expression by mutableStateOf("")
        private set
    var result by mutableStateOf("")
        private set
    var memory by mutableStateOf(0.0)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun append(text: String) { error = null; expression += text }
    fun clear() { expression = ""; result = ""; error = null }
    fun allClear() { clear(); memory = 0.0 }
    fun backspace() { if (expression.isNotEmpty()) expression = expression.dropLast(1) }
    fun toggleSign() {
        // wrap current expression
        if (expression.isEmpty()) return
        expression = if (expression.startsWith("-")) expression.drop(1) else "-($expression)"
    }

    fun evaluate() {
        if (expression.isBlank()) return
        val angle = if (settings.value.angleMode == com.c4.calculator.data.settings.AngleMode.DEG)
            EngineAngleMode.DEG else EngineAngleMode.RAD
        val parser = ExpressionParser(angle)
        try {
            val value = parser.evaluate(expression)
            val formatted = format(value, settings.value.decimalPlaces)
            result = formatted
            error = null
            viewModelScope.launch {
                db.historyDao().insert(
                    HistoryEntity(expression = expression, result = formatted, timestamp = System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            error = e.message ?: "Error"
            result = "Error"
        }
    }

    fun memoryClear() { memory = 0.0 }
    fun memoryRecall() { expression += format(memory, settings.value.decimalPlaces) }
    fun memoryAdd() { evaluateIfNeeded(); memory += currentResultValue() }
    fun memorySubtract() { evaluateIfNeeded(); memory -= currentResultValue() }
    fun memoryStore() { evaluateIfNeeded(); memory = currentResultValue() }

    private fun evaluateIfNeeded() {
        if (result.isNotEmpty() && result != "Error" && expression.isNotEmpty()) {
            // keep
        }
        if (expression.isNotEmpty() && result.isEmpty()) evaluate()
    }

    private fun currentResultValue(): Double =
        result.toDoubleOrNull() ?: expression.toDoubleOrNull() ?: 0.0

    private fun format(value: Double, places: Int): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        val rounded = String.format(java.util.Locale.US, "%.${places}f", value)
        // trim trailing zeros? keep simple but trim
        var s = rounded
        if (s.contains(".")) {
            s = s.trimEnd('0').trimEnd('.')
        }
        if (s.isEmpty() || s == "-0") s = "0"
        // scientific for huge
        if (value != 0.0 && (kotlin.math.abs(value) >= 1e15 || kotlin.math.abs(value) < 1e-10)) {
            s = value.toString()
        }
        return s
    }

    fun loadExpression(v: String) { expression = v }
}
