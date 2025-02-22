package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.domain.model.ProductFilter

interface ProductLocalDataSource {
    suspend fun getProducts(filter: ProductFilter?): Result<List<ProductDetails>>
    suspend fun insertProduct(productDetails: ProductDetails): Result<Unit> // Unit - означает, что функция просто выполняет действие и не возвращает значения
    suspend fun updateProduct(productDetails: ProductDetails): Result<Unit>
}