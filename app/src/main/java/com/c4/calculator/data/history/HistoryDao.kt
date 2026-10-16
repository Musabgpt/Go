package com.c4.calculator.data.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insert(entity: HistoryEntity)

    @Delete
    suspend fun delete(entity: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}
