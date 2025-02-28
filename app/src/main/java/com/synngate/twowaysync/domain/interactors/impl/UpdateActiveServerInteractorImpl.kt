package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ActiveServerRepository
import com.synngate.twowaysync.domain.interactors.UpdateActiveServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer

class UpdateActiveServerInteractorImpl(
    private val activeServerRepository: ActiveServerRepository
) : UpdateActiveServerInteractor {

    override suspend fun execute(externalServer: ExternalServer, isActive: Boolean) {
        activeServerRepository.update(externalServer = externalServer, isActive = isActive)
    }
}