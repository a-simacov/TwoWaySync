package com.synngate.twowaysync.di

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val CURRENT_SERVER_ID_KEY = intPreferencesKey("current_server_id") // Ключ для ID текущего сервера
}