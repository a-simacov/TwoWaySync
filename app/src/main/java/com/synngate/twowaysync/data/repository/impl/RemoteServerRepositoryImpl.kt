package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.domain.model.RemoteServerDetails

class RemoteServerRepositoryImpl(private val remoteServerLocalDataSource: RemoteServerLocalDataSource) : RemoteServerRepository {

    override suspend fun getRemoteServers(): Result<List<RemoteServerDetails>> {
        return remoteServerLocalDataSource.getRemoteServers()
    }

    override suspend fun insertRemoteServer(serverDetails: RemoteServerDetails): Result<Unit> {
        return remoteServerLocalDataSource.insertRemoteServer(serverDetails)
    }
}