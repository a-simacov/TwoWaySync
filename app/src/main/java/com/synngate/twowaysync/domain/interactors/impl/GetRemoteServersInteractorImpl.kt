package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.domain.interactors.GetRemoteServersInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails

class GetRemoteServersInteractorImpl(private val remoteServerRepository: RemoteServerRepository) : GetRemoteServersInteractor {

    override suspend fun invoke(): Result<List<RemoteServerDetails>> {
        // Здесь может быть бизнес-логика, связанная с получением списка серверов, например, сортировка, кэширование
        return remoteServerRepository.getRemoteServers()
    }
}