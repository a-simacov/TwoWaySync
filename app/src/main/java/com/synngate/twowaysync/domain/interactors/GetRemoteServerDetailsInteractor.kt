package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

interface GetRemoteServerDetailsInteractor {
    suspend fun execute(serverId: Int): Flow<RemoteServerDetails?>
}