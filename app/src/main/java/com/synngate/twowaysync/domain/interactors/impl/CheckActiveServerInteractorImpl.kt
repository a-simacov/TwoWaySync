package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ActiveServerRepository
import com.synngate.twowaysync.domain.interactors.CheckActiveServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer

class CheckActiveServerInteractorImpl(
    private val activeServerRepository: ActiveServerRepository
) : CheckActiveServerInteractor {

    override suspend fun execute(externalServer: ExternalServer): Boolean {
        return activeServerRepository.isActive(externalServer)
    }
}