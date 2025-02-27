package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(private val logLocalDataSource: LogLocalDataSource) : LogRepository {

    override suspend fun getLogs(filter: LogFilter?): Flow<List<LogDetails>> {
        return logLocalDataSource.getLogs(filter)
    }

    override suspend fun insertLog(logDetails: LogDetails): Result<Unit> {
        return logLocalDataSource.insertLog(logDetails)
    }

    override suspend fun getLogsCount(): Result<Int> {
        return logLocalDataSource.getLogsCount()
    }

    override suspend fun deleteAll() {
        logLocalDataSource.deleteAll()
    }
}