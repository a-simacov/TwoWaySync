package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

interface CheckServerAvailabilityInteractor {
    fun execute(serverDetails: ExternalServer): Flow<Boolean>
}