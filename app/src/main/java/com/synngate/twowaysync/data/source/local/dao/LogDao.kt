package com.synngate.twowaysync.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity

@Dao
interface LogDao {
    @Query("SELECT * FROM logs")
    suspend fun getAll(): List<LogDetailsEntity>

    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: LogDetailsEntity)

    @Query("SELECT COUNT(*) FROM logs")
    suspend fun getLogsCount(): Int
}