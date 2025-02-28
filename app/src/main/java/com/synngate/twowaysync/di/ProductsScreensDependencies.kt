package com.synngate.twowaysync.di

import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.domain.interactors.DeleteProductsInteractor
import com.synngate.twowaysync.domain.interactors.GetProductsInteractor
import com.synngate.twowaysync.domain.interactors.UpdateProductsInteractor
import com.synngate.twowaysync.domain.interactors.impl.DeleteProductsInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetProductsInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.UpdateProductsInteractorImpl

class ProductsScreensDependencies(
    private val appDependencies: AppDependencies
) {

    private val productRepository: ProductRepository by lazy {
        appDependencies.productRepository
    }

    val getProductsInteractor: GetProductsInteractor by lazy {
        GetProductsInteractorImpl(productRepository)
    }

    val deleteProductsInteractor: DeleteProductsInteractor by lazy {
        DeleteProductsInteractorImpl(productRepository)
    }

    val updateProductsInteractor: UpdateProductsInteractor by lazy {
        UpdateProductsInteractorImpl(productRepository, appDependencies.activeApiService!!)
    }

    fun clear() {

    }
}