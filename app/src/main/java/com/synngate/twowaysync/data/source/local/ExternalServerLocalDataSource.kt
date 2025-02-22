package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

interface ExternalServerLocalDataSource {
    suspend fun insert(server: ExternalServer): Int
    suspend fun update(server: ExternalServer)
    suspend fun delete(serverId: Int)
    suspend fun getServer(serverId: Int): Flow<ExternalServer?>
    fun getAllServers(): Flow<List<ExternalServer>>
}