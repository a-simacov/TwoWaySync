package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.RemoteServerDetails

interface RemoteServerLocalDataSource {
    suspend fun getRemoteServers(): Result<List<RemoteServerDetails>>
    suspend fun insertRemoteServer(serverDetails: RemoteServerDetails): Result<Unit> // Unit - означает, что функция просто выполняет действие и не возвращает значения
}