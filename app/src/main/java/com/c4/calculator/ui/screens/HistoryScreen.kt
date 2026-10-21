package com.c4.calculator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.c4.calculator.data.history.HistoryEntity
import com.c4.calculator.ui.viewmodel.CalculatorViewModel
import com.c4.calculator.ui.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    vm: HistoryViewModel,
    calcVm: CalculatorViewModel,
    padding: androidx.compose.foundation.layout.PaddingValues
) {
    val items by vm.items.collectAsState()
    Column(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = { vm.clearAll() }) { Text("Clear All") }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(items) { item ->
                HistoryRow(item, onClick = {
                    calcVm.setExpression(item.expression)
                }, onDelete = { vm.delete(item) })
            }
        }
    }
}

@Composable
private fun HistoryRow(item: HistoryEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable { onClick() }.padding(8.dp)) {
        Text(item.expression, style = MaterialTheme.typography.bodyLarge)
        Text("= ${item.result}", style = MaterialTheme.typography.bodyMedium)
        Row {
            Text(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp)),
                style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
            androidx.compose.material3.TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}
