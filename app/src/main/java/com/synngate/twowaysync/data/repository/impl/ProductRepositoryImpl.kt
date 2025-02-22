package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.domain.interactors.impl.ProductLocalDataSource
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.domain.model.ProductFilter

class ProductRepositoryImpl(private val productLocalDataSource: ProductLocalDataSource) : ProductRepository {

    override suspend fun getProducts(filter: ProductFilter?): Result<List<ProductDetails>> {
        // Временная реализация без фильтрации (пока фильтрация не реализована в LocalDataSource)
        return productLocalDataSource.getProducts(filter)
    }

    override suspend fun insertProduct(productDetails: ProductDetails): Result<Unit> {
        return productLocalDataSource.insertProduct(productDetails)
    }

    override suspend fun updateProduct(productDetails: ProductDetails): Result<Unit> {
        return productLocalDataSource.updateProduct(productDetails)
    }
}