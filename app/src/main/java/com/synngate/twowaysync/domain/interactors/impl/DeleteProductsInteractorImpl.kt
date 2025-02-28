package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.domain.interactors.DeleteProductsInteractor


class DeleteProductsInteractorImpl(private val productRepository: ProductRepository) :
    DeleteProductsInteractor {

    override suspend fun execute() {
        productRepository.deleteAll()
    }
}