package com.synngate.twowaysync.domain.manager.impl

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.synngate.twowaysync.di.DataStoreKeys
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import kotlinx.coroutines.flow.first
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import androidx.datastore.preferences.core.stringPreferencesKey

class RemoteServerConnectionManagerImpl(
    private val dataStore: DataStore<Preferences> // <----  Добавили DataStore в конструктор
) : RemoteServerConnectionManager {

    private val TAG = "RemoteServerConnManager" // <----  Для логирования

    override fun checkServerAvailability(host: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(InetAddress.getByName(host), port), 2000) // Timeout 2 секунды
            socket.close()
            true // Соединение установлено успешно, сервер доступен
        } catch (e: Exception) {
            false // Ошибка соединения, сервер недоступен
        }
    }

    override fun getRemoteServerStatus(): String { // <----  ДОБАВЛЯЕМ реализацию метода getRemoteServerStatus()
        // TODO:  Реализовать получение реального статуса удаленного сервера
        return "Неизвестно" //  Временно возвращаем "Неизвестно" в качестве заглушки
    }

    override suspend fun setCurrentServerId(serverId: Int?) { // <----  Реализация setCurrentServerId
        Log.d(TAG, "setCurrentServerId: serverId = $serverId") // <----  Логирование

        dataStore.edit { preferences ->
            preferences[DataStoreKeys.CURRENT_SERVER_ID_KEY] = serverId ?: 0 // Сохраняем ID как строку
        }
    }

    override suspend fun getCurrentServerId(): Int? { // <----  Реализация getCurrentServerId
        val preferences = dataStore.data.first()
        val serverId = preferences[DataStoreKeys.CURRENT_SERVER_ID_KEY] // Читаем ID как строку
        Log.d(TAG, "getCurrentServerId: serverId = $serverId") // <----  Логирование
        return serverId // Возвращаем Int?
    }
}