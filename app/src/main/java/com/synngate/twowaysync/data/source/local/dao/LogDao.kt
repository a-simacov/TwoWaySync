package com.synngate.twowaysync.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM logs")
    fun getAll(): Flow<List<LogDetailsEntity>>

    @Insert
    suspend fun insert(log: LogDetailsEntity)

    @Query("SELECT COUNT(*) FROM logs")
    suspend fun getLogsCount(): Int
}