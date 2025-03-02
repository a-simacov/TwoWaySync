package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.flow.Flow

interface LogLocalDataSource {

    fun getLogs(filter: LogFilter?): Flow<List<LogDetails>>

    suspend fun insertLog(logDetails: LogDetails): Result<Unit>

    fun getLogsCount(): Flow<Int>

    suspend fun deleteAll()
}