package com.synngate.twowaysync.data.remote

import com.synngate.twowaysync.data.remote.api.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

class ApiServiceFactory {
    private val client = OkHttpClient.Builder().build()
    private val instances = ConcurrentHashMap<String, ApiService>()

    fun create(baseUrl: String): ApiService {
        return instances.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
