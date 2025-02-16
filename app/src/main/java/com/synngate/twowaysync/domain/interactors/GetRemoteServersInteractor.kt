package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

interface GetRemoteServersInteractor { // <---- Интерфейс GetRemoteServersInteractor
    fun execute(): Flow<List<RemoteServerDetails>> // <---- Метод execute, возвращающий Flow<List<RemoteServerDetails>>
}