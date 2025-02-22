package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer

interface SaveExternalServerInteractor {

    suspend fun execute(server: ExternalServer): Int
}