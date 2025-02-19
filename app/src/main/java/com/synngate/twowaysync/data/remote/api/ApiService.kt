package com.synngate.twowaysync.data.remote.api

import com.synngate.twowaysync.data.remote.models.ServerResponse
import com.synngate.twowaysync.data.remote.models.ProductDto
import retrofit2.http.GET

interface ApiService {
    @GET("server/status")
    suspend fun getServerStatus(): ServerResponse

    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}
