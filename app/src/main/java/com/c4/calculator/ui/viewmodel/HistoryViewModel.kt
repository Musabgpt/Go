package com.c4.calculator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.c4.calculator.data.history.AppDatabase
import com.c4.calculator.data.history.HistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).historyDao()
    val items: StateFlow<List<HistoryEntity>> = dao.getAll().stateIn(
        viewModelScope, SharingStarted.Eagerly, emptyList()
    )
    fun delete(item: HistoryEntity) = viewModelScope.launch { dao.delete(item) }
    fun clearAll() = viewModelScope.launch { dao.clearAll() }
}
