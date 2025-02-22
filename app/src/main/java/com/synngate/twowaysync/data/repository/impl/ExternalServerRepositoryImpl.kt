package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

class ExternalServerRepositoryImpl(
    private val externalServerLocalDataSource: ExternalServerLocalDataSource
) : ExternalServerRepository {

    override suspend fun insert(server: ExternalServer): Int {
        return externalServerLocalDataSource.insert(server)
    }

    override suspend fun update(server: ExternalServer) {
        externalServerLocalDataSource.update(server)
    }

    override suspend fun delete(serverId: Int) {
        externalServerLocalDataSource.delete(serverId)
    }

    override suspend fun getServer(serverId: Int): Flow<ExternalServer?> {
        return externalServerLocalDataSource.getServer(serverId)
    }

    override fun getAllServers(): Flow<List<ExternalServer>> {
        return externalServerLocalDataSource.getAllServers()
    }
}