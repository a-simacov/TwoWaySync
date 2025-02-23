package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    suspend fun getLogs(filter: LogFilter?): Flow<List<LogDetails>>

    suspend fun insertLog(logDetails: LogDetails): Result<Unit>

    suspend fun getLogsCount(): Result<Int>
}