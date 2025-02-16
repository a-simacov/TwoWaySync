package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.LogDetails

interface GetLogDetailsInteractor {
    suspend fun invoke(logId: Int): Result<LogDetails>
}