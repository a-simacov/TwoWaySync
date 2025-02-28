package com.synngate.twowaysync.di

import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.domain.interactors.DeleteLogsInteractor
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.interactors.impl.DeleteLogsInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetLogsInteractorImpl

class LogsScreensDpenedencies(
    private val appDependencies: AppDependencies
) {

    private val logRepository: LogRepository by lazy {
        appDependencies.logRepository
    }

    val getLogsInteractor: GetLogsInteractor by lazy {
        GetLogsInteractorImpl(logRepository)
    }

    val deleteLogsInteractor: DeleteLogsInteractor by lazy {
        DeleteLogsInteractorImpl(logRepository)
    }

    fun clear() {

    }
}