package com.synngate.twowaysync.di

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val CURRENT_SERVER_ID_KEY = stringPreferencesKey("current_server_id") // Ключ для ID текущего сервера
}