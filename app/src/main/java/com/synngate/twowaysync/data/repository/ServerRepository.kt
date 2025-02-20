package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.data.local.datastore.PreferencesManager
import com.synngate.twowaysync.data.local.db.dao.ExternalServersDao
import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity
import com.synngate.twowaysync.data.remote.ApiServiceFactory
import com.synngate.twowaysync.data.remote.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

class ServerRepository(
    private val preferencesManager: PreferencesManager,
    private val externalServersDao: ExternalServersDao,
    private val apiServiceFactory: ApiServiceFactory
) {

    suspend fun getActiveServerApi(): ApiService? {
        val serverId = preferencesManager.activeServerId.first() ?: return null
        val server = externalServersDao.getById(serverId).firstOrNull() ?: return null
        return apiServiceFactory.create("https://${server.host}:${server.port}")
    }

    suspend fun checkServerStatus(): Boolean {
        val apiService = getActiveServerApi() ?: return false
        return try {
            apiService.getServerStatus().isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun authenticateServer(server: ExternalServerEntity): Response<Unit> {
        val api = apiServiceFactory.create("https://${server.host}:${server.port}")
        return api.getServerStatus()
    }

    fun getServersFlow(): Flow<List<ExternalServerEntity>> =
        externalServersDao.getAll()

    fun getActiveServerIdFlow(): Flow<Int?> =
        preferencesManager.activeServerId

    suspend fun isActiveServer(id: Int): Boolean {
        return id == getActiveServerIdFlow().firstOrNull()
    }

    suspend fun setActiveServer(serverId: Int) {
        preferencesManager.setActiveServerId(serverId)
    }

    suspend fun deleteServer(serverId: Int) {
        externalServersDao.deleteById(serverId)
        val activeId = preferencesManager.activeServerId.first()
        if (activeId == serverId) {
            preferencesManager.setActiveServerId(-1)
        }
    }

    suspend fun getServerById(id: Int): Flow<ExternalServerEntity?> =
        externalServersDao.getById(id)

    suspend fun saveServer(server: ExternalServerEntity): Int {
        return externalServersDao.insertOrUpdate(server).toInt()
    }
}
