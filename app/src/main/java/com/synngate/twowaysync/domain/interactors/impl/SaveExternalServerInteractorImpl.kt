package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.domain.model.ExternalServer
import com.synngate.twowaysync.domain.interactors.SaveExternalServerInteractor
import kotlinx.coroutines.delay

class SaveExternalServerInteractorImpl(
    private val externalServerRepository: ExternalServerRepository
) : SaveExternalServerInteractor {

    override suspend fun execute(server: ExternalServer): Int {
        delay(1000)
        return externalServerRepository.insert(server)
    }
}