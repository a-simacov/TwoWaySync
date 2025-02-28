package com.synngate.twowaysync.data.source.local.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.synngate.twowaysync.data.source.local.ActiveServerDataSource
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.first

class ActiveServerDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : ActiveServerDataSource {

    override suspend fun isActive(externalServer: ExternalServer): Boolean {
        val preferences = dataStore.data.first()
        val savedServerId = preferences[CURRENT_SERVER_ID_KEY]
        return savedServerId == externalServer.id
    }

    override suspend fun update(externalServer: ExternalServer, isActive: Boolean) {
        val externalServerId = if (isActive) -1 else externalServer.id!!
        dataStore.edit { prefs ->
            prefs[CURRENT_SERVER_ID_KEY] = externalServerId
        }

        AppDependencies.setCurrentApiService(externalServerId)
    }
}