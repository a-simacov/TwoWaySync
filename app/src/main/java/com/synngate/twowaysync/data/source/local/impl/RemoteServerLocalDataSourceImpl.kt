package com.synngate.twowaysync.data.source.local.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.data.source.local.dao.RemoteServerDao
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteServerLocalDataSourceImpl(
    private val remoteServerDao: RemoteServerDao
) : RemoteServerLocalDataSource {

    override suspend fun insert(server: RemoteServerDetails) {
        remoteServerDao.insert(server)
    }

    override suspend fun update(server: RemoteServerDetails) {
        remoteServerDao.update(server)
    }

    override suspend fun delete(serverId: Int) {
        remoteServerDao.delete(serverId)
    }

    override suspend fun getServer(serverId: Int): RemoteServerDetails? {
        return remoteServerDao.getServer(serverId)
    }

    override fun getAllServers(): Flow<List<RemoteServerDetails>> {
        return remoteServerDao.getAllServers()
    }
}