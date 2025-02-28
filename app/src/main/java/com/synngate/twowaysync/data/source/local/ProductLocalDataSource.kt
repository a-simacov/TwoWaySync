package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.domain.model.ProductDetails
import kotlinx.coroutines.flow.Flow

interface ProductLocalDataSource {

    fun getProducts(): Flow<List<ProductDetails>>
    suspend fun insertProducts(products: List<ProductDetails>)
    suspend fun getProductsCount(): Int
    suspend fun deleteAll()
}