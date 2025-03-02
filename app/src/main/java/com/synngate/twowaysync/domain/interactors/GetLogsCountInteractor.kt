package com.synngate.twowaysync.domain.interactors

import kotlinx.coroutines.flow.Flow

interface GetLogsCountInteractor {

    suspend fun execute(): Flow<Int>
}