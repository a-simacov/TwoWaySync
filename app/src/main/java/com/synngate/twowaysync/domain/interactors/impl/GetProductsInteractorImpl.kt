package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.domain.interactors.GetProductsInteractor
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.domain.model.ProductFilter

class GetProductsInteractorImpl(private val productRepository: ProductRepository) : GetProductsInteractor {

    override suspend fun invoke(filter: ProductFilter?): Result<List<ProductDetails>> {
        // Здесь может быть бизнес-логика получения списка товаров, например, кэширование, обогащение данных
        return productRepository.getProducts(filter)
    }
}