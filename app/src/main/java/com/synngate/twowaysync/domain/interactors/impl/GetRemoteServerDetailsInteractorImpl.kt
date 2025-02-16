package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.domain.interactors.GetRemoteServerDetailsInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails

class GetRemoteServerDetailsInteractorImpl(private val remoteServerRepository: RemoteServerRepository) : GetRemoteServerDetailsInteractor {

    override suspend fun invoke(serverId: Int): Result<RemoteServerDetails> {
        // Здесь может быть бизнес-логика получения деталей сервера, например, проверка прав доступа, логирование
        // В текущей реализации просто заглушка - нужно будет реализовать получение деталей сервера
        return Result.Failure(Exception("Not implemented yet - GetRemoteServerDetailsInteractorImpl")) // TODO: Реализовать получение деталей сервера
    }
}