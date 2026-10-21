package com.c4.calculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.c4.calculator.data.settings.CalcMode
import com.c4.calculator.ui.viewmodel.CalculatorViewModel
import com.c4.calculator.ui.viewmodel.SettingsViewModel

@Composable
fun CalculatorScreen(
    vm: CalculatorViewModel,
    settingsVm: SettingsViewModel,
    padding: androidx.compose.foundation.layout.PaddingValues
) {
    val settings by settingsVm.settings.collectAsState()
    val mode = settings.calcMode
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(8.dp)
    ) {
        // Display
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = vm.expression,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                maxLines = 3
            )
            Text(
                text = if (vm.error != null) vm.error!! else vm.result,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                maxLines = 2
            )
        }
        // Mode selector
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            CalcMode.values().forEach { m ->
                Button(onClick = { settingsVm.setCalcMode(m) }) { Text(m.name.take(4)) }
            }
        }
        // Buttons
        when (mode) {
            CalcMode.STANDARD -> StandardButtons(vm)
            CalcMode.SCIENTIFIC -> ScientificButtons(vm)
            CalcMode.PROGRAMMER -> ProgrammerButtons(vm)
        }
    }
}

@Composable
private fun RowScope.btn(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, weight: Float = 1f) {
    Button(onClick = onClick, modifier = modifier.weight(weight).padding(2.dp)) {
        Text(label, fontSize = 18.sp)
    }
}

@Composable
private fun StandardButtons(vm: CalculatorViewModel) {
    val rows = listOf(
        listOf("AC", "C", "%", "/"),
        listOf("7", "8", "9", "*"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "+/-", "=")
    )
    Column(Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { label ->
                    btn(label, onClick = {
                        when (label) {
                            "AC" -> vm.allClear()
                            "C" -> vm.clear()
                            "+/-" -> vm.toggleSign()
                            "=" -> vm.evaluate()
                            else -> vm.append(label)
                        }
                    })
                }
            }
        }
        // Memory row
        Row(Modifier.fillMaxWidth()) {
            listOf("MC", "MR", "M+", "M-", "MS").forEach { label ->
                btn(label, onClick = {
                    when (label) {
                        "MC" -> vm.memoryClear()
                        "MR" -> vm.memoryRecall()
                        "M+" -> vm.memoryAdd()
                        "M-" -> vm.memorySubtract()
                        "MS" -> vm.memoryStore()
                    }
                })
            }
        }
    }
}

@Composable
private fun ScientificButtons(vm: CalculatorViewModel) {
    StandardButtons(vm)
    Column(Modifier.fillMaxWidth()) {
        val funcs = listOf(
            listOf("sin", "cos", "tan", "log", "ln"),
            listOf("sinh", "cosh", "tanh", "sqrt", "cbrt"),
            listOf("asin", "acos", "atan", "x^2", "x^3"),
            listOf("x^y", "10^x", "e^x", "1/x", "!"),
            listOf("abs", "pi", "e", "(", ")")
        )
        funcs.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { label ->
                    btn(label, onClick = {
                        when (label) {
                            "x^2" -> vm.append("^2")
                            "x^3" -> vm.append("^3")
                            "x^y" -> vm.append("^")
                            "10^x" -> vm.append("10^(")
                            "e^x" -> vm.append("exp(")
                            "1/x" -> vm.append("1/(")
                            "!" -> vm.append("!")
                            "pi" -> vm.append("pi")
                            "e" -> vm.append("e")
                            "(" -> vm.append("(")
                            ")" -> vm.append(")")
                            else -> vm.append("$label(")
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun ProgrammerButtons(vm: CalculatorViewModel) {
    StandardButtons(vm)
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            listOf("and(", "or(", "xor(", "not(", "shl(").forEach { label ->
                btn(label.trimEnd('('), onClick = { vm.append(label) })
            }
        }
        Row(Modifier.fillMaxWidth()) {
            listOf("shr(", "0x", "0b", "0o").forEach { label ->
                btn(label, onClick = { vm.append(label) })
            }
        }
    }
}
