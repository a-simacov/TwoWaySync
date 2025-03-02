package com.synngate.twowaysync.di

import com.synngate.twowaysync.domain.interactors.GetLogsCountInteractor
import com.synngate.twowaysync.domain.interactors.GetProductsCountInteractor
import com.synngate.twowaysync.domain.interactors.impl.GetLogsCountInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetProductsCountInteractorImpl
import com.synngate.twowaysync.services.ActualServerCheckDataStore
import com.synngate.twowaysync.services.WebServerCheckDataStore

class MainScreenDependencies(
    private val appDependencies: AppDependencies
) {

    val getLogsCountInteractor: GetLogsCountInteractor by lazy {
        GetLogsCountInteractorImpl(appDependencies.logRepository)
    }

    val getProductsCountInteractor: GetProductsCountInteractor by lazy {
        GetProductsCountInteractorImpl(appDependencies.productRepository)
    }

    val actualServerCheckDataStore =
        ActualServerCheckDataStore(dataStore = AppDependencies.dataStore)
    val webServerCheckDataStore = WebServerCheckDataStore(dataStore = AppDependencies.dataStore)

    fun clear() {

    }
}