package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

interface CheckServerAvailabilityInteractor { // <---- Интерфейс CheckServerAvailabilityInteractor
    fun execute(serverDetails: RemoteServerDetails): Flow<Boolean> // <---- Метод execute, принимающий RemoteServerDetails и возвращающий Flow<Boolean>
}