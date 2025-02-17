package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.data.source.local.dao.RemoteServerDao
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

class RemoteServerRepositoryImpl(
    private val remoteServerLocalDataSource: RemoteServerLocalDataSource // <---- Зависимость от RemoteServerLocalDataSource
) : RemoteServerRepository {

    override suspend fun insert(server: RemoteServerDetails) {
        remoteServerLocalDataSource.insert(server) // <---- Используем LocalDataSource
    }

    override suspend fun update(server: RemoteServerDetails) {
        remoteServerLocalDataSource.update(server) // <---- Используем LocalDataSource
    }

    override suspend fun delete(serverId: Int) {
        remoteServerLocalDataSource.delete(serverId) // <---- Используем LocalDataSource
    }

    override suspend fun getServer(serverId: Int): Flow<RemoteServerDetails?> {
        return remoteServerLocalDataSource.getServer(serverId) // <---- Используем LocalDataSource
    }

    override fun getAllServers(): Flow<List<RemoteServerDetails>> {
        return remoteServerLocalDataSource.getAllServers() // <---- Используем LocalDataSource
    }
}