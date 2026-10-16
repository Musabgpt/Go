package com.c4.calculator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.c4.calculator.data.settings.ThemeMode
import com.c4.calculator.data.settings.CalcMode
import com.c4.calculator.data.settings.AngleMode
import com.c4.calculator.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(vm: SettingsViewModel, padding: androidx.compose.foundation.layout.PaddingValues) {
    val s by vm.settings.collectAsState()
    Column(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
        Text("Theme")
        Row { ThemeMode.values().forEach { t ->
            Button(onClick = { vm.setTheme(t) }, modifier = Modifier.padding(2.dp)) { Text(t.name) }
        } }
        Text("Calculator Mode")
        Row { CalcMode.values().forEach { m ->
            Button(onClick = { vm.setCalcMode(m) }, modifier = Modifier.padding(2.dp)) { Text(m.name) }
        } }
        Text("Angle Mode")
        Row { AngleMode.values().forEach { a ->
            Button(onClick = { vm.setAngleMode(a) }, modifier = Modifier.padding(2.dp)) { Text(a.name) }
        } }
        Row(Modifier.padding(vertical = 4.dp)) {
            Text("Haptic", Modifier.weight(1f))
            Switch(checked = s.haptic, onCheckedChange = { vm.setHaptic(it) })
        }
        Row(Modifier.padding(vertical = 4.dp)) {
            Text("Sound", Modifier.weight(1f))
            Switch(checked = s.sound, onCheckedChange = { vm.setSound(it) })
        }
        Button(onClick = { vm.clearHistory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application) }) {
            Text("Clear History")
        }
        Text("About: Calculator v1.0", Modifier.padding(top = 16.dp))
    }
}
