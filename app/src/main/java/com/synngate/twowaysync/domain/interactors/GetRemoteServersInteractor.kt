package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.RemoteServerDetails

interface GetRemoteServersInteractor {
    suspend fun invoke(): Result<List<RemoteServerDetails>>
}