package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.domain.interactors.GetProductsInteractor

class GetProductsInteractorImpl(private val repository: ProductRepository) : GetProductsInteractor {
    override suspend fun execute() = repository.getProducts()
}