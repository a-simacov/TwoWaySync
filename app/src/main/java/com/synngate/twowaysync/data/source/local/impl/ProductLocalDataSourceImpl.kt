package com.synngate.twowaysync.data.source.local.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.source.local.dao.ProductDao
import com.synngate.twowaysync.domain.interactors.impl.ProductLocalDataSource
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.domain.model.ProductFilter
import com.synngate.twowaysync.data.source.local.entity.ProductDetailsEntity

class ProductLocalDataSourceImpl(private val productDao: ProductDao) : ProductLocalDataSource {

    override suspend fun getProducts(filter: ProductFilter?): Result<List<ProductDetails>> {
        return try {
            // Временная реализация без фильтрации (пока не реализована логика фильтрации)
            val productEntities = productDao.getAll()
            val productDetailsList = productEntities.map { entity ->
                ProductDetails(
                    id = entity.id,
                    name = entity.name,
                    barcode = entity.barcode,
                    modified = entity.modified
                )
            }
            Result.Success(productDetailsList)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun insertProduct(productDetails: ProductDetails): Result<Unit> {
        return try {
            val productEntity = ProductDetailsEntity(
                id = productDetails.id, // ID должен быть передан из domain model (не автогенерация)
                name = productDetails.name,
                barcode = productDetails.barcode,
                modified = productDetails.modified
            )
            productDao.insert(productEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateProduct(productDetails: ProductDetails): Result<Unit> {
        return try {
            val productEntity = ProductDetailsEntity(
                id = productDetails.id, // ID для обновления должен быть передан из domain model
                name = productDetails.name,
                barcode = productDetails.barcode,
                modified = productDetails.modified
            )
            // TODO: Реализовать метод update в ProductDao и вызвать productDao.update(productEntity)
            // Пока просто имитация успешного обновления
            Result.Success(Unit) // Имитация успешного обновления
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}