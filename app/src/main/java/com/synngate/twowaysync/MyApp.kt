package com.synngate.twowaysync

import android.app.Application
import com.synngate.twowaysync.data.local.datastore.PreferencesManager
import com.synngate.twowaysync.data.local.db.AppDatabase
import com.synngate.twowaysync.data.remote.ApiServiceFactory
import com.synngate.twowaysync.data.repository.ServerRepository

class MyApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var serverRepository: ServerRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        serverRepository = ServerRepository(
            externalServersDao = database.externalServersDao(),
            preferencesManager = preferencesManager,
            apiServiceFactory = ApiServiceFactory()
        )
    }
}
