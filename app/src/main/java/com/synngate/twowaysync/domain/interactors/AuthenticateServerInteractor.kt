package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

interface AuthenticateServerInteractor { // <---- Интерфейс AuthenticateServerInteractor
    fun execute(serverDetails: ExternalServer): Flow<Boolean> // <---- Метод execute, принимающий RemoteServerDetails и возвращающий Flow<Boolean>
}