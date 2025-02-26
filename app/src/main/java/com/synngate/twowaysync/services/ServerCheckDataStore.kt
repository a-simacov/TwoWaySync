package com.synngate.twowaysync.services

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServerCheckDataStore(private val dataStore: DataStore<Preferences>) {

    private val SERVER_STATUS_KEY = stringPreferencesKey("server_status")
    private val SERVER_CHECK_TIME_KEY = stringPreferencesKey("server_check_time")
    private val IS_SERVICE_RUNNING_KEY = booleanPreferencesKey("is_service_running")

    suspend fun saveServerStatus(status: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_STATUS_KEY] = status
        }
    }

    val serverStatusFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SERVER_STATUS_KEY] ?: "Ожидание проверки"
        }

    suspend fun saveServerCheckTime(time: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_CHECK_TIME_KEY] = time
        }
    }

    val serverCheckTimeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SERVER_CHECK_TIME_KEY] ?: "Нет данных"
        }

    suspend fun saveServiceRunningState(isRunning: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_SERVICE_RUNNING_KEY] = isRunning
        }
    }

    val serviceRunningStateFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_SERVICE_RUNNING_KEY] ?: false
        }
}