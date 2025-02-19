package com.synngate.twowaysync.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    private val ACTIVE_SERVER_ID = intPreferencesKey("ACTIVE_SERVER_ID")

    val activeServerId: Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[ACTIVE_SERVER_ID] }

    suspend fun setActiveServerId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_SERVER_ID] = id
        }
    }

    suspend fun clearActiveServer() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACTIVE_SERVER_ID)
        }
    }
}
