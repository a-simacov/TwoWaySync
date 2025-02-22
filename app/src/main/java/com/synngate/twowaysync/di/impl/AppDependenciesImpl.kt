package com.synngate.twowaysync.di.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.repository.impl.LogRepositoryImpl
import com.synngate.twowaysync.data.repository.impl.ProductRepositoryImpl
import com.synngate.twowaysync.data.repository.impl.ExternalServerRepositoryImpl
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.domain.interactors.impl.ProductLocalDataSource
import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.LogLocalDataSourceImpl
import com.synngate.twowaysync.data.source.local.impl.ProductLocalDataSourceImpl
import com.synngate.twowaysync.data.source.local.impl.ExternalServerLocalDataSourceImpl
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.interactors.impl.GetMainScreenDataInteractorImpl
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import com.synngate.twowaysync.domain.manager.impl.RemoteServerConnectionManagerImpl
import com.synngate.twowaysync.domain.service.LocalWebServerService
import com.synngate.twowaysync.domain.service.impl.LocalWebServerServiceImpl
import com.synngate.twowaysync.ui.screens.main.MainScreenViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppDependenciesImpl(private val appContext: Context) : AppDependencies {

    override val dataStore: DataStore<Preferences> by lazy {
        appContext.dataStore
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(appContext)
    }

    private val logLocalDataSource: LogLocalDataSource by lazy {
        LogLocalDataSourceImpl(database.logDao())
    }

    private val externalServerLocalDataSource: ExternalServerLocalDataSource by lazy {
        ExternalServerLocalDataSourceImpl(database.externalServerDao())
    }

    private val productLocalDataSource: ProductLocalDataSource by lazy {
        ProductLocalDataSourceImpl(database.productDao())
    }

    private val logRepository: LogRepository by lazy {
        LogRepositoryImpl(logLocalDataSource)
    }

    private val externalServerRepository: ExternalServerRepository by lazy {
        ExternalServerRepositoryImpl(externalServerLocalDataSource)
    }

    private val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(productLocalDataSource)
    }

    private val getMainScreenDataInteractor: GetMainScreenDataInteractor by lazy {
        GetMainScreenDataInteractorImpl(
            logRepository,
            externalServerRepository,
            productRepository,
            remoteServerConnectionManager,
            localWebServerService
        )
    }

    val remoteServerConnectionManager: RemoteServerConnectionManager by lazy {
        RemoteServerConnectionManagerImpl(dataStore)
    }

    private val localWebServerService: LocalWebServerService by lazy {
        LocalWebServerServiceImpl()
    }

    // Presentation Layer - ViewModels implementations
    override fun getMainScreenViewModel(): MainScreenViewModel {
        return MainScreenViewModel(getMainScreenDataInteractor)
    }

    // Data Source Layer
    override fun provideLogLocalDataSource(): LogLocalDataSource = logLocalDataSource
    override fun provideRemoteServerLocalDataSource(): ExternalServerLocalDataSource = externalServerLocalDataSource
    override fun provideProductLocalDataSource(): ProductLocalDataSource = productLocalDataSource

    // Data Repository Layer
    override fun provideLogRepository(): LogRepository = logRepository
    override fun provideRemoteServerRepository(): ExternalServerRepository = externalServerRepository
    override fun provideProductRepository(): ProductRepository = productRepository

    // Domain Layer - Interactors
    override fun provideGetMainScreenDataInteractor(): GetMainScreenDataInteractor = getMainScreenDataInteractor

    // Domain Layer - Managers
//    override fun provideLocalWebServerManager(): LocalWebServerManager = localWebServerManager
    override fun provideRemoteServerConnectionManager(): RemoteServerConnectionManager = remoteServerConnectionManager

    // Domain Layer - Services
    override fun provideLocalWebServerService(): LocalWebServerService = localWebServerService
}