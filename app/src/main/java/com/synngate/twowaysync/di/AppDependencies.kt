package com.synngate.twowaysync.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.repository.impl.ExternalServerRepositoryImpl
import com.synngate.twowaysync.data.repository.impl.LogRepositoryImpl
import com.synngate.twowaysync.data.repository.impl.ProductRepositoryImpl
import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.ExternalServerLocalDataSourceImpl
import com.synngate.twowaysync.data.source.local.impl.LogLocalDataSourceImpl
import com.synngate.twowaysync.data.source.local.impl.ProductLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.impl.ProductLocalDataSource
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import com.synngate.twowaysync.domain.manager.impl.RemoteServerConnectionManagerImpl
import com.synngate.twowaysync.domain.service.LocalWebServerService
import com.synngate.twowaysync.domain.service.impl.LocalWebServerServiceImpl

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

object AppDependencies {

    lateinit var applicationContext: Context
        private set

    val dataStore: DataStore<Preferences> by lazy {
        applicationContext.dataStore
    }

    fun init(context: Context) {
        applicationContext = context
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    val logLocalDataSource: LogLocalDataSource by lazy {
        LogLocalDataSourceImpl(database.logDao())
    }

    val externalServerLocalDataSource: ExternalServerLocalDataSource by lazy {
        ExternalServerLocalDataSourceImpl(database.externalServerDao())
    }

    val productLocalDataSource: ProductLocalDataSource by lazy {
        ProductLocalDataSourceImpl(database.productDao())
    }

    val logRepository: LogRepository by lazy {
        LogRepositoryImpl(logLocalDataSource)
    }

    val externalServerRepository: ExternalServerRepository by lazy {
        ExternalServerRepositoryImpl(externalServerLocalDataSource)
    }

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(productLocalDataSource)
    }

    val remoteServerConnectionManager: RemoteServerConnectionManager by lazy {
        RemoteServerConnectionManagerImpl(dataStore)
    }

    val localWebServerService: LocalWebServerService by lazy {
        LocalWebServerServiceImpl()
    }

    // Data Repository Layer
    fun provideLogRepository(): LogRepository = logRepository
    fun provideRemoteServerRepository(): ExternalServerRepository = externalServerRepository

    // Domain Layer - Managers
//    override fun provideLocalWebServerManager(): LocalWebServerManager = localWebServerManager
    fun provideRemoteServerConnectionManager(): RemoteServerConnectionManager =
        remoteServerConnectionManager

    // Domain Layer - Services
    fun provideLocalWebServerService(): LocalWebServerService = localWebServerService
}