package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

interface AuthenticateServerInteractor { // <---- Интерфейс AuthenticateServerInteractor
    fun execute(serverDetails: RemoteServerDetails): Flow<Boolean> // <---- Метод execute, принимающий RemoteServerDetails и возвращающий Flow<Boolean>
}