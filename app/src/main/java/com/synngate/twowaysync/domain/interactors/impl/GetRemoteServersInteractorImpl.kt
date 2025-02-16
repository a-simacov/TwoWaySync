package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.domain.interactors.GetRemoteServersInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

class GetRemoteServersInteractorImpl(
    private val remoteServerRepository: RemoteServerRepository // <---- Зависимость от RemoteServerRepository
) : GetRemoteServersInteractor { // <---- Реализация интерфейса GetRemoteServersInteractor

    override fun execute(): Flow<List<RemoteServerDetails>> { // <---- Реализация метода execute()
        return remoteServerRepository.getAllServers() // <---- Вызываем метод getAllServers() репозитория и возвращаем Flow
    }
}