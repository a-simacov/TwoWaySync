package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.domain.common.LogFilter

interface GetLogsInteractor {
    suspend fun invoke(filter: LogFilter?): Result<List<LogDetails>>
}