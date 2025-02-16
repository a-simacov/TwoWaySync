package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.model.LogDetails

class GetLogsInteractorImpl(private val logRepository: LogRepository) : GetLogsInteractor {

    override suspend fun invoke(filter: LogFilter?): Result<List<LogDetails>> {
        // Здесь может быть бизнес-логика фильтрации или обработки логов перед возвратом Presentation Layer
        return logRepository.getLogs(filter)
    }
}