package com.synngate.twowaysync.di

import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.DeleteExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.GetExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.GetExternalServersInteractor
import com.synngate.twowaysync.domain.interactors.SaveExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.DeleteExternalServerInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetExternalServerInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetExternalServersInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.SaveExternalServerInteractorImpl

class ServersGraphDependencies(
    private val appDependencies: AppDependencies
) {

    val getExternalServersInteractor: GetExternalServersInteractor by lazy {
        GetExternalServersInteractorImpl(appDependencies.externalServerRepository)
    }

    val getExternalServerInteractor: GetExternalServerInteractor by lazy {
        GetExternalServerInteractorImpl(appDependencies.externalServerRepository)
    }

    val saveExternalServerInteractor: SaveExternalServerInteractor by lazy {
        SaveExternalServerInteractorImpl(appDependencies.externalServerRepository)
    }

    val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor by lazy {
        CheckServerAvailabilityInteractorImpl()
    }

    val deleteExternalServerInteractor: DeleteExternalServerInteractor by lazy {
        DeleteExternalServerInteractorImpl(appDependencies.externalServerRepository)
    }

    fun clear() {

    }
}