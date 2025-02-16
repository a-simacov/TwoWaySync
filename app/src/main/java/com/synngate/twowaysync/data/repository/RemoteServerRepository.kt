package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.RemoteServerDetails

interface RemoteServerRepository {
    suspend fun getRemoteServers(): Result<List<RemoteServerDetails>>
    suspend fun insertRemoteServer(serverDetails: RemoteServerDetails): Result<Unit>
}