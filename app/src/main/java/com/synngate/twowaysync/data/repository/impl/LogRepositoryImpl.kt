package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.util.LogHelper

class LogRepositoryImpl(private val logLocalDataSource: LogLocalDataSource) : LogRepository {

    override suspend fun getLogs(filter: LogFilter?): Result<List<LogDetails>> {
        LogHelper.log("LogRepositoryImpl.getLogs() called") // <----  Используем Singleton LogHelper напрямую
        val result = logLocalDataSource.getLogs(filter)
        LogHelper.log("LogRepositoryImpl.getLogs() finished, result: $result") // <---- Используем Singleton LogHelper напрямую
        return result
    }

    override suspend fun insertLog(logDetails: LogDetails): Result<Unit> {
        return logLocalDataSource.insertLog(logDetails)
    }

    override suspend fun getLogsCount(): Result<Int> {
        return logLocalDataSource.getLogsCount()
    }
}