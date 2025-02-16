package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

interface RemoteServerLocalDataSource {
    suspend fun insert(server: RemoteServerDetails)
    suspend fun update(server: RemoteServerDetails)
    suspend fun delete(serverId: Int)
    suspend fun getServer(serverId: Int): RemoteServerDetails?
    fun getAllServers(): Flow<List<RemoteServerDetails>>
}