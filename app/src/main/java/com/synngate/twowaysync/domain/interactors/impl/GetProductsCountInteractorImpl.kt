package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.domain.interactors.GetProductsCountInteractor
import kotlinx.coroutines.flow.Flow

class GetProductsCountInteractorImpl(
    private val productRepository: ProductRepository
) : GetProductsCountInteractor {

    override suspend fun execute(): Flow<Int> {
        return productRepository.getProductsCount()
    }
}