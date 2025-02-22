package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

interface GetExternalServerInteractor {
    suspend fun execute(serverId: Int): Flow<ExternalServer?>
}