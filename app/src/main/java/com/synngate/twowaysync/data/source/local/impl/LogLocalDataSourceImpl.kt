package com.synngate.twowaysync.data.source.local.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.data.source.local.dao.LogDao
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LogLocalDataSourceImpl(
    private val logDao: LogDao
) : LogLocalDataSource {

    override fun getLogs(filter: LogFilter?): Flow<List<LogDetails>> {
        return if (filter == null) {
            logDao.getFilteredLogs(null, null, null, null)
        } else {
            logDao.getFilteredLogs(
                filter.event,
                filter.level,
                filter.dateTimeFrom,
                filter.dateTimeTo
            )
        }.map { logEntities ->
            logEntities.map { logEntityToLogDetails(it) }
        }
    }

    override suspend fun insertLog(logDetails: LogDetails): Result<Unit> {
        return try {
            val logEntity = logDetailsToLogEntity(logDetails)
            withContext(Dispatchers.IO) {
                logDao.insert(logEntity)
            }
            Result.Success(Unit)

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getLogsCount(): Result<Int> {
        val count = withContext(Dispatchers.IO) {
            logDao.getLogsCount()
        }
        return Result.Success(count)
    }

    private fun logEntityToLogDetails(logEntity: LogDetailsEntity): LogDetails {
        return LogDetails(
            id = logEntity.id,
            event = logEntity.event,
            level = logEntity.level,
            dateTime = logEntity.dateTime
        )
    }

    private fun logDetailsToLogEntity(logDetails: LogDetails): LogDetailsEntity {
        return LogDetailsEntity(
            id = logDetails.id,
            event = logDetails.event,
            level = logDetails.level,
            dateTime = logDetails.dateTime
        )
    }
}