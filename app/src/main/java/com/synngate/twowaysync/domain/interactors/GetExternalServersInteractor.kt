package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

interface GetExternalServersInteractor {
    fun execute(): Flow<List<ExternalServer>>
}