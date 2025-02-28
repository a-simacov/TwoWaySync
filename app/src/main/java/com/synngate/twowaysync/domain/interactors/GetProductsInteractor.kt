package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.ProductDetails
import kotlinx.coroutines.flow.Flow

interface GetProductsInteractor {
    suspend fun execute(): Flow<List<ProductDetails>>
}