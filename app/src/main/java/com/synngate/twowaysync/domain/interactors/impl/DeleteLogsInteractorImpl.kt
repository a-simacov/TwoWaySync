package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.domain.interactors.DeleteLogsInteractor

class DeleteLogsInteractorImpl(private val logRepository: LogRepository) : DeleteLogsInteractor {

    override suspend fun execute() {
        logRepository.deleteAll()
    }
}