package com.synngate.twowaysync.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface LogDao {

    @Query(
        """
        SELECT * FROM logs 
        WHERE (:event IS NULL OR event LIKE '%' || :event || '%') 
        AND (:level IS NULL OR level = :level) 
        AND (:dateTimeFrom IS NULL OR date_time >= :dateTimeFrom) 
        AND (:dateTimeTo IS NULL OR date_time <= :dateTimeTo)
    """
    )
    fun getFilteredLogs(
        event: String?,
        level: String?,
        dateTimeFrom: LocalDateTime?,
        dateTimeTo: LocalDateTime?
    ): Flow<List<LogDetailsEntity>>

    @Insert
    suspend fun insert(log: LogDetailsEntity)

    @Query("SELECT COUNT(*) FROM logs")
    suspend fun getLogsCount(): Int

    @Query("DELETE FROM logs")
    suspend fun deleteAll()
}
