package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.domain.interactors.GetRemoteServerDetailsInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

class GetRemoteServerDetailsInteractorImpl(private val remoteServerRepository: RemoteServerRepository) : GetRemoteServerDetailsInteractor {

    override suspend fun execute(serverId: Int): Flow<RemoteServerDetails?> { // <---- Реализация метода execute()
        return remoteServerRepository.getServer(serverId) // <---- Вызываем метод getServerById() репозитория и возвращаем Flow
    }}