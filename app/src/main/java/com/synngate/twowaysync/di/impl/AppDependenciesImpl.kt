package com.synngate.twowaysync.di.impl

import android.content.Context
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.repository.impl.LogRepositoryImpl
import com.synngate.twowaysync.data.repository.impl.ProductRepositoryImpl
import com.synngate.twowaysync.data.repository.impl.RemoteServerRepositoryImpl
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.data.source.local.ProductLocalDataSource
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.LogLocalDataSourceImpl
import com.synngate.twowaysync.data.source.local.impl.ProductLocalDataSourceImpl
import com.synngate.twowaysync.data.source.local.impl.RemoteServerLocalDataSourceImpl
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.interactors.impl.GetMainScreenDataInteractorImpl
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import com.synngate.twowaysync.domain.manager.impl.RemoteServerConnectionManagerImpl
import com.synngate.twowaysync.domain.service.LocalWebServerService
import com.synngate.twowaysync.domain.service.impl.LocalWebServerServiceImpl
import com.synngate.twowaysync.presentation.main.MainScreenViewModel

class AppDependenciesImpl(private val appContext: Context) : AppDependencies { // <----  Реализация интерфейса AppDependencies

    private val database: AppDatabase by lazy { // Lazy инициализация базы данных
        AppDatabase.getDatabase(appContext)
    }

    // Data Source Layer implementations
    private val logLocalDataSource: LogLocalDataSource by lazy {
        LogLocalDataSourceImpl(database.logDao())
    }

    private val remoteServerLocalDataSource: RemoteServerLocalDataSource by lazy {
        RemoteServerLocalDataSourceImpl(database.remoteServerDao())
    }

    private val productLocalDataSource: ProductLocalDataSource by lazy {
        ProductLocalDataSourceImpl(database.productDao())
    }

    // Data Repository Layer implementations
    private val logRepository: LogRepository by lazy {
        LogRepositoryImpl(logLocalDataSource)
    }

    private val remoteServerRepository: RemoteServerRepository by lazy {
        RemoteServerRepositoryImpl(remoteServerLocalDataSource)
    }

    private val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(productLocalDataSource)
    }

    // Domain Layer - Interactors implementations
    private val getMainScreenDataInteractor: GetMainScreenDataInteractor by lazy {
        GetMainScreenDataInteractorImpl(
            logRepository, // <----  LogRepository
            remoteServerRepository, // <----  RemoteServerRepository
            productRepository, // <----  ProductRepository
            remoteServerConnectionManager,
            localWebServerService
        )
    }

    // Domain Layer - Managers implementations
    private val remoteServerConnectionManager: RemoteServerConnectionManager by lazy {
        RemoteServerConnectionManagerImpl() // Пока без зависимостей
    }

    // Domain Layer - Services implementations
    private val localWebServerService: LocalWebServerService by lazy {
        LocalWebServerServiceImpl() // Пока без зависимостей и контекста
    }

    // Presentation Layer - ViewModels implementations
    override fun getMainScreenViewModel(): MainScreenViewModel { // <----  Реализация метода интерфейса getMainScreenViewModel()
        return MainScreenViewModel(getMainScreenDataInteractor)
    }

    // Data Source Layer
    override fun provideLogLocalDataSource(): LogLocalDataSource = logLocalDataSource
    override fun provideRemoteServerLocalDataSource(): RemoteServerLocalDataSource = remoteServerLocalDataSource
    override fun provideProductLocalDataSource(): ProductLocalDataSource = productLocalDataSource

    // Data Repository Layer
    override fun provideLogRepository(): LogRepository = logRepository
    override fun provideRemoteServerRepository(): RemoteServerRepository = remoteServerRepository
    override fun provideProductRepository(): ProductRepository = productRepository

    // Domain Layer - Interactors
//    override fun provideGetLogCountInteractor(): GetLogCountInteractor = getLogCountInteractor
//    override fun provideGetRemoteServerCountInteractor(): GetRemoteServerCountInteractor = getRemoteServerCountInteractor
//    override fun provideGetProductCountInteractor(): GetProductCountInteractor = getProductCountInteractor
    override fun provideGetMainScreenDataInteractor(): GetMainScreenDataInteractor = getMainScreenDataInteractor

    // Domain Layer - Managers
//    override fun provideLocalWebServerManager(): LocalWebServerManager = localWebServerManager
    override fun provideRemoteServerConnectionManager(): RemoteServerConnectionManager = remoteServerConnectionManager

    // Domain Layer - Services
    override fun provideLocalWebServerService(): LocalWebServerService = localWebServerService
}