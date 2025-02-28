package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.source.local.ProductLocalDataSource
import com.synngate.twowaysync.domain.model.ProductDetails

class ProductRepositoryImpl(private val localDataSource: ProductLocalDataSource) :
    ProductRepository {

    override suspend fun getProducts() = localDataSource.getProducts()
    override suspend fun insertProducts(products: List<ProductDetails>) =
        localDataSource.insertProducts(products)

    override suspend fun getProductsCount() = localDataSource.getProductsCount()
    override suspend fun deleteAll() = localDataSource.deleteAll()
}