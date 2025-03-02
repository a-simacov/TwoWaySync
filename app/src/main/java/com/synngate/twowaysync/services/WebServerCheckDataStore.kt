package com.synngate.twowaysync.services

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WebServerCheckDataStore(private val dataStore: DataStore<Preferences>) {

    private companion object {
        //        private val SERVER_STATUS_KEY = stringPreferencesKey("actual_server_status")
//        private val SERVER_CHECK_TIME_KEY = stringPreferencesKey("actual_server_check_time")
        private val IS_CHECK_RUNNING_KEY = booleanPreferencesKey("is_web_server_running")
    }

//    val serverStatusFlow: Flow<String> = dataStore.data
//        .map { preferences ->
//            preferences[SERVER_STATUS_KEY] ?: "Ожидание проверки"
//        }
//
//    val serverCheckTimeFlow: Flow<String> = dataStore.data
//        .map { preferences ->
//            preferences[SERVER_CHECK_TIME_KEY] ?: "Нет данных"
//        }

    val serviceRunningStateFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_CHECK_RUNNING_KEY] ?: false
        }

//    suspend fun saveServerStatus(status: String) {
//        dataStore.edit { preferences ->
//            preferences[SERVER_STATUS_KEY] = status
//        }
//    }
//
//    suspend fun saveServerCheckTime(time: String) {
//        dataStore.edit { preferences ->
//            preferences[SERVER_CHECK_TIME_KEY] = time
//        }
//    }

    suspend fun saveServiceRunningState(isRunning: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CHECK_RUNNING_KEY] = isRunning
        }
    }
}