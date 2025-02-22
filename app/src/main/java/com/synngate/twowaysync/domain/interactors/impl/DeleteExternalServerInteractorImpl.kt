package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.domain.interactors.DeleteExternalServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.delay

class DeleteExternalServerInteractorImpl(
    private val externalServerRepository: ExternalServerRepository
) : DeleteExternalServerInteractor {

    override suspend fun execute(server: ExternalServer) {
        delay(1000)
        val serverId = server.id
        if (serverId != null)
            externalServerRepository.delete(serverId)
    }
}