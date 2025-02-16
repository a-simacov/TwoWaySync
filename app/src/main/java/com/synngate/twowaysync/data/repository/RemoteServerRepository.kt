package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

interface RemoteServerRepository {

    suspend fun insert(server: RemoteServerDetails) // <---- Добавлен метод insert
    suspend fun update(server: RemoteServerDetails) // <---- Добавлен метод update
    suspend fun delete(serverId: Int)             // <---- Добавлен метод delete
    suspend fun getServer(serverId: Int): RemoteServerDetails? // <---- Добавлен метод getServer
    fun getAllServers(): Flow<List<RemoteServerDetails>> // <---- Метод getAllServers УЖЕ был, но убедитесь, что он Flow<List<...>>

    // suspend fun saveRemoteServer(remoteServer: RemoteServerDetails) // <----  Этот метод был ранее, удалите его, если он есть, или закомментируйте - он не нужен.
}