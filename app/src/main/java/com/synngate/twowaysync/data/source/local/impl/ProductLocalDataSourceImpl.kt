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

    override suspend fun getProductsCount(): Int = productDao.getProductsCount()

    override suspend fun deleteAll() = productDao.deleteAll()

//    override suspend fun getProducts(filter: ProductFilter?): Result<List<ProductDetails>> {
//        return try {
//            // Временная реализация без фильтрации (пока не реализована логика фильтрации)
//            val productEntities = productDao.getAll()
//            val productDetailsList = productEntities.map { entity ->
//                ProductDetails(
//                    id = entity.id,
//                    name = entity.name,
//                    barcode = entity.barcode,
//                    modified = entity.modified
//                )
//            }
//            Result.Success(productDetailsList)
//        } catch (e: Exception) {
//            Result.Failure(e)
//        }
//    }
//
//    override suspend fun insertProduct(productDetails: ProductDetails): Result<Unit> {
//        return try {
//            val productEntity = ProductDetailsEntity(
//                id = productDetails.id, // ID должен быть передан из domain model (не автогенерация)
//                name = productDetails.name,
//                barcode = productDetails.barcode,
//                modified = productDetails.modified
//            )
//            productDao.insert(productEntity)
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            Result.Failure(e)
//        }
//    }
//
//    override suspend fun updateProduct(productDetails: ProductDetails): Result<Unit> {
//        return try {
//            val productEntity = ProductDetailsEntity(
//                id = productDetails.id, // ID для обновления должен быть передан из domain model
//                name = productDetails.name,
//                barcode = productDetails.barcode,
//                modified = productDetails.modified
//            )
//            // TODO: Реализовать метод update в ProductDao и вызвать productDao.update(productEntity)
//            // Пока просто имитация успешного обновления
//            Result.Success(Unit) // Имитация успешного обновления
//        } catch (e: Exception) {
//            Result.Failure(e)
//        }
//    }

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