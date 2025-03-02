package com.synngate.twowaysync.data.source.local.impl

import com.synngate.twowaysync.data.source.local.ProductLocalDataSource
import com.synngate.twowaysync.data.source.local.dao.ProductDao
import com.synngate.twowaysync.data.source.local.entity.ProductDetailsEntity
import com.synngate.twowaysync.domain.model.ProductDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductLocalDataSourceImpl(private val productDao: ProductDao) : ProductLocalDataSource {

    override fun getProducts(): Flow<List<ProductDetails>> =
        productDao.getAllProducts().map { entities -> entities.map { productEntityToDetails(it) } }

    override suspend fun insertProducts(products: List<ProductDetails>) =
        productDao.insertProducts(products.map { productDetailsToEntity(it) })

    override fun getProductsCount() = productDao.getProductsCount()

    override suspend fun deleteAll() = productDao.deleteAll()

    private fun productEntityToDetails(entity: ProductDetailsEntity) = ProductDetails(
        id = entity.id,
        barcode = entity.barcode,
        name = entity.name
    )

    private fun productDetailsToEntity(details: ProductDetails) = ProductDetailsEntity(
        id = details.id,
        barcode = details.barcode,
        name = details.name
    )
}