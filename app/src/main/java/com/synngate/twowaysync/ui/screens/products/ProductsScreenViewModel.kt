package com.synngate.twowaysync.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.domain.interactors.DeleteProductsInteractor
import com.synngate.twowaysync.domain.interactors.GetProductsInteractor
import com.synngate.twowaysync.domain.interactors.UpdateProductsInteractor
import com.synngate.twowaysync.domain.model.ProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductsScreenViewModel(
    private val getProductsInteractor: GetProductsInteractor,
    private val updateProductsInteractor: UpdateProductsInteractor,
    private val deleteProductsInteractor: DeleteProductsInteractor,
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _productsCount = MutableStateFlow(0)
    val productsCount: StateFlow<Int> = _productsCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _serverStatus = MutableStateFlow("Checking server status...")
    val serverStatus: StateFlow<String> = _serverStatus.asStateFlow()

    init {
        loadProducts()
        observeServerStatus()
    }

    private fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            getProductsInteractor.execute()
                .combine(_searchQuery) { products, query ->
                    if (query.isEmpty()) products
                    else products.filter {
                        it.name.contains(query, ignoreCase = true) || it.barcode.contains(query)
                    }
                }
                .collect { filteredProducts ->
                    _products.value = filteredProducts
                    _productsCount.value = filteredProducts.size
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun updateProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val result = updateProductsInteractor.execute()
            _isLoading.value = false
            if (result.isSuccess) {
                _message.value = "Products updated successfully"
            } else {
                val exceptionMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                _message.value = "Update failed: $exceptionMessage"
            }
        }
    }

    fun clearProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteProductsInteractor.execute()
            _message.value = "Products cleared"
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun observeServerStatus() {
        viewModelScope.launch {
            WorkManager.getInstance(AppDependencies.applicationContext)
                .getWorkInfosForUniqueWorkLiveData("PingActiveServerWorker")
                .asFlow()
                .collect { workInfos ->
                    val status = workInfos.firstOrNull()?.state?.let {
                        when (it) {
                            WorkInfo.State.SUCCEEDED -> "Server is available"
                            WorkInfo.State.FAILED -> "Server is unavailable"
                            else -> "Checking server status..."
                        }
                    } ?: "No status available"
                    _serverStatus.value = status
                }
        }
    }

//    fun onBackClicked() {
//        navController.navigate("main_screen") {
//            popUpTo(navController.graph.startDestinationId)
//        }
//        viewModelStore.clear()
//    }
}