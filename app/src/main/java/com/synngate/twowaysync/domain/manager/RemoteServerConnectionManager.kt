package com.synngate.twowaysync.domain.manager

interface RemoteServerConnectionManager {
    fun checkServerAvailability(host: String, port: Int): Boolean // Проверяет доступность сервера
    fun getRemoteServerStatus(): String
    suspend fun setCurrentServerId(serverId: Int?)
    suspend fun getCurrentServerId(): Int?
}