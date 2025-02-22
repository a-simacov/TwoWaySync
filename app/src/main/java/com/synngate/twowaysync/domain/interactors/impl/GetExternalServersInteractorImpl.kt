package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.domain.interactors.GetExternalServersInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

class GetExternalServersInteractorImpl(
    private val externalServerRepository: ExternalServerRepository
) : GetExternalServersInteractor {

    override fun execute(): Flow<List<ExternalServer>> {
        return externalServerRepository.getAllServers()
    }
}