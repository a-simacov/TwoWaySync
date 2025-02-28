package com.synngate.twowaysync.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.di.ProductsScreensDependencies

class ProductsScreenViewModelFactory(
    private val productsScreensDependencies: ProductsScreensDependencies
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return with(productsScreensDependencies) {
                ProductsScreenViewModel(
                    getProductsInteractor,
                    updateProductsInteractor,
                    deleteProductsInteractor
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}