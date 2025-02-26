package com.synngate.twowaysync.di

import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetMainScreenDataInteractorImpl
import com.synngate.twowaysync.services.ServerCheckDataStore

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

    val serverCheckDataStore = ServerCheckDataStore(dataStore = AppDependencies.dataStore)

    fun clear() {

    }
}