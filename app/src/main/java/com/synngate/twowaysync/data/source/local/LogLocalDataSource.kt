package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.model.LogDetails

interface LogLocalDataSource {
    suspend fun getLogs(filter: LogFilter?): Result<List<LogDetails>>
    suspend fun insertLog(logDetails: LogDetails): Result<Unit> // Unit - означает, что функция просто выполняет действие и не возвращает значения
    suspend fun getLogsCount(): Result<Int>
}