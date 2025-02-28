package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.source.remote.ApiService
import com.synngate.twowaysync.domain.interactors.UpdateProductsInteractor

class UpdateProductsInteractorImpl(
    private val repository: ProductRepository,
    private val apiService: ApiService // Интерфейс API, см. ниже
) : UpdateProductsInteractor {

    override suspend fun execute(): Result<Unit> {
        try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                repository.insertProducts(products)
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}