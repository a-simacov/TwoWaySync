package com.synngate.twowaysync.domain.manager

interface RemoteServerConnectionManager {
    fun checkServerAvailability(host: String, port: Int): Boolean // Проверяет доступность сервера
    fun getRemoteServerStatus(): String
}