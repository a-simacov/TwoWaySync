package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.domain.model.ProductFilter

interface ProductRepository {
    suspend fun getProducts(filter: ProductFilter?): Result<List<ProductDetails>>
    suspend fun insertProduct(productDetails: ProductDetails): Result<Unit>
    suspend fun updateProduct(productDetails: ProductDetails): Result<Unit>
}