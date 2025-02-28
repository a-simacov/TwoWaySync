package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer

interface UpdateActiveServerInteractor {

    suspend fun execute(externalServer: ExternalServer, isActive: Boolean)
}