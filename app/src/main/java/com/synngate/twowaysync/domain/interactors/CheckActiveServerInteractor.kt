package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer

interface CheckActiveServerInteractor {

    suspend fun execute(externalServer: ExternalServer): Boolean
}