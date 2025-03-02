package com.synngate.twowaysync.di

import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetMainScreenDataInteractorImpl
import com.synngate.twowaysync.services.ActualServerCheckDataStore
import com.synngate.twowaysync.services.WebServerCheckDataStore

class MainScreenDependencies(
    private val appDependencies: AppDependencies
) {

    val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor by lazy {
        CheckServerAvailabilityInteractorImpl()
    }

    val getMainScreenDataInteractor: GetMainScreenDataInteractor by lazy {
        with(appDependencies) {
            GetMainScreenDataInteractorImpl(
                logRepository,
                externalServerRepository,
                productRepository,
                remoteServerConnectionManager,
                localWebServerService
            )
        }
    }

    val actualServerCheckDataStore =
        ActualServerCheckDataStore(dataStore = AppDependencies.dataStore)
    val webServerCheckDataStore = WebServerCheckDataStore(dataStore = AppDependencies.dataStore)

    fun clear() {

    }
}