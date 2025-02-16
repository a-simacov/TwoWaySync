package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.domain.model.ProductFilter

interface GetProductsInteractor {
    suspend fun invoke(filter: ProductFilter?): Result<List<ProductDetails>>
}