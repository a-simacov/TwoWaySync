package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.flow.Flow

interface GetLogsInteractor {
    suspend fun execute(filter: LogFilter?): Flow<List<LogDetails>>
}