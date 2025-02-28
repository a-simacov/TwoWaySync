package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.domain.interactors.SaveExternalServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer

class SaveExternalServerInteractorImpl(
    private val externalServerRepository: ExternalServerRepository
) : SaveExternalServerInteractor {

    override suspend fun execute(server: ExternalServer): Int {
        return externalServerRepository.insert(server)
    }
}