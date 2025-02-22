package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer

interface DeleteExternalServerInteractor {

    suspend fun execute(server: ExternalServer)
}