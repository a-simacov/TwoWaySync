package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.domain.model.ProductDetails
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun getProducts(): Flow<List<ProductDetails>>

    suspend fun insertProducts(products: List<ProductDetails>)

    suspend fun getProductsCount(): Flow<Int>

    suspend fun deleteAll()
}