package com.synngate.twowaysync.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.domain.interactors.DeleteProductsInteractor
import com.synngate.twowaysync.domain.interactors.GetProductsInteractor
import com.synngate.twowaysync.domain.interactors.UpdateProductsInteractor
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.services.PingActiveServerWorker
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
) : ViewModel(), ProductsScreenViewModelInterface {

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    override val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _productsCount = MutableStateFlow(0)
    override val productsCount: StateFlow<Int> = _productsCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    override val message: StateFlow<String?> = _message.asStateFlow()

    private val _serverStatus = MutableStateFlow("Checking server status...")
    override val serverStatus: StateFlow<String> = _serverStatus.asStateFlow()

    init {
        loadProducts()
        scheduleServerPing()
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

    override fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    override fun updateProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val result = updateProductsInteractor.execute()
            _isLoading.value = false
            if (result.isSuccess) {
                _message.value = "Обновление товаров выполнено"
            } else {
                val exceptionMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _message.value = "Ошибка при обновлении: $exceptionMessage"
            }
        }
    }

    override fun clearProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteProductsInteractor.execute()
            _message.value = "Все товары удалены"
        }
    }

    override fun clearMessage() {
        _message.value = null
    }

    private fun scheduleServerPing() {
        val workManager = WorkManager.getInstance(AppDependencies.applicationContext)
        val pingRequest = OneTimeWorkRequestBuilder<PingActiveServerWorker>().build()
        workManager.enqueueUniqueWork(
            "PingActiveServerWorker",
            ExistingWorkPolicy.KEEP,
            pingRequest
        )
    }

    private fun observeServerStatus() {
        viewModelScope.launch {
            WorkManager.getInstance(AppDependencies.applicationContext)
                .getWorkInfosForUniqueWorkLiveData("PingActiveServerWorker")
                .asFlow()
                .collect { workInfos ->
                    val status = workInfos.firstOrNull()?.state?.let {
                        when (it) {
                            WorkInfo.State.SUCCEEDED -> "Сервер доступен"
                            WorkInfo.State.FAILED -> "Сервер недоступен"
                            WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> "Проверка статуса сервера..."
                            else -> "Проверка статуса сервера не запущена"
                        }
                    } ?: "Нет статуса проверки сервера"
                    _serverStatus.value = status
                }
        }
    }
}