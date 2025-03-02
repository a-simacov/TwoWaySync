package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.domain.interactors.GetLogsCountInteractor
import kotlinx.coroutines.flow.Flow

class GetLogsCountInteractorImpl(
    private val logRepository: LogRepository
) : GetLogsCountInteractor {

    override suspend fun execute(): Flow<Int> {
        return logRepository.getLogsCount()
    }
}