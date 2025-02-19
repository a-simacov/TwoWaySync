package com.synngate.twowaysync.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.synngate.twowaysync.data.remote.api.ApiService

class RetrofitProvider {

    private var retrofit: Retrofit? = null

    fun createApiService(baseUrl: String): ApiService {
        if (retrofit == null || retrofit!!.baseUrl().toString() != baseUrl) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}
