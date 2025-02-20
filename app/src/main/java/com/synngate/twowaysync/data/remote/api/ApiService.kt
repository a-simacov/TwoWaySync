package com.synngate.twowaysync.data.remote.api

import com.synngate.twowaysync.data.remote.models.ProductDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("server/status")
    suspend fun getServerStatus(): Response<Unit>

    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}
