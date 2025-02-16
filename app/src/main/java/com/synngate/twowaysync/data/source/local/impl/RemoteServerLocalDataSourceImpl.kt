package com.synngate.twowaysync.data.source.local.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.source.local.dao.RemoteServerDao
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import com.synngate.twowaysync.data.source.local.entity.RemoteServerEntity

class RemoteServerLocalDataSourceImpl(private val remoteServerDao: RemoteServerDao) : RemoteServerLocalDataSource {

    override suspend fun getRemoteServers(): Result<List<RemoteServerDetails>> {
        return try {
            val serverEntities = remoteServerDao.getAll()
            val serverDetailsList = serverEntities.map { entity ->
                RemoteServerDetails(
                    id = entity.id,
                    name = entity.name,
                    host = entity.host,
                    port = entity.port,
                    username = entity.username
                )
            }
            Result.Success(serverDetailsList)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun insertRemoteServer(serverDetails: RemoteServerDetails): Result<Unit> {
        return try {
            val serverEntity = RemoteServerEntity(
                name = serverDetails.name,
                host = serverDetails.host,
                port = serverDetails.port,
                username = serverDetails.username
            )
            remoteServerDao.insert(serverEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}