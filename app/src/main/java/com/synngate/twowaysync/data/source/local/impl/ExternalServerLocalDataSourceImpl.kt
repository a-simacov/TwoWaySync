package com.synngate.twowaysync.data.source.local.impl

import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.data.source.local.dao.ExternalServerDao
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

class ExternalServerLocalDataSourceImpl(
    private val externalServerDao: ExternalServerDao
) : ExternalServerLocalDataSource {

    override suspend fun insert(server: ExternalServer): Int {
        return externalServerDao.insert(server).toInt()
    }

    override suspend fun update(server: ExternalServer) {
        externalServerDao.update(server)
    }

    override suspend fun delete(serverId: Int) {
        externalServerDao.delete(serverId)
    }

    override suspend fun getServer(serverId: Int): Flow<ExternalServer?> {
        return externalServerDao.getServer(serverId)
    }

    override fun getAllServers(): Flow<List<ExternalServer>> {
        return externalServerDao.getAllServers()
    }
}