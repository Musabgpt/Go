package com.c4.calculator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.c4.calculator.converter.UnitCategory
import com.c4.calculator.converter.UnitConverter

@Composable
fun ConverterScreen(padding: androidx.compose.foundation.layout.PaddingValues) {
    var category by remember { mutableStateOf(UnitCategory.LENGTH) }
    var from by remember { mutableStateOf(UnitConverter.units(category).first().name) }
    var to by remember { mutableStateOf(UnitConverter.units(category).last().name) }
    var input by remember { mutableStateOf("1") }
    val result = runCatching {
        UnitConverter.convert(category, input.toDoubleOrNull() ?: 0.0, from, to)
    }.getOrNull()?.toString() ?: ""

    Column(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
        // Category selector
        Row(Modifier.fillMaxWidth()) {
            UnitCategory.values().forEach { c ->
                OutlinedButton(onClick = {
                    category = c
                    from = UnitConverter.units(c).first().name
                    to = UnitConverter.units(c).last().name
                }, modifier = Modifier.padding(2.dp)) { Text(c.name.take(4)) }
            }
        }
        UnitPicker("From", UnitConverter.units(category).map { it.name }, from) { from = it }
        UnitPicker("To", UnitConverter.units(category).map { it.name }, to) { to = it }
        TextField(value = input, onValueChange = { input = it }, label = { Text("Value") }, modifier = Modifier.fillMaxWidth())
        Text("Result: $result", modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun UnitPicker(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(4.dp)) {
        Text("$label: $selected", modifier = Modifier.weight(1f))
        OutlinedButton(onClick = { expanded = true }) { Text("Change") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}
