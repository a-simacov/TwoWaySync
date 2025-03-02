package com.synngate.twowaysync.domain.interactors

import kotlinx.coroutines.flow.Flow

interface GetProductsCountInteractor {

    suspend fun execute(): Flow<Int>
}