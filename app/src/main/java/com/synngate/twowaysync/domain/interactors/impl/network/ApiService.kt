package com.synngate.twowaysync.domain.interactors.impl.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("/") //  Предполагаем, что echoEndpoint - это корень сервера, можно изменить на "/echo" или другой путь
    suspend fun echo(): Response<Void> //  Echo endpoint, ожидаем пустой ответ, только статус код

//    @GET("/") //  Предполагаем, что echoEndpoint - это корень сервера, можно изменить на "/echo" или другой путь
//    suspend fun echo2(): Response<Void> //  Echo endpoint, ожидаем пустой ответ, только статус код

//    @GET("/") //  Путь к authEndpoint (ЗАМЕНИТЬ НА РЕАЛЬНЫЙ PATH, если отличается)
//    suspend fun authenticate(
//        @Header("Authorization") authHeader: String?, // <---- Заголовок Authorization, теперь nullable (String?)
//        @Body authRequest: AuthRequest // <---- Тело запроса типа AuthRequest, аннотация @Body
//    ): Response<Void>

//    @POST("/auth") //  Путь к authEndpoint (ЗАМЕНИТЬ НА РЕАЛЬНЫЙ PATH, если отличается)
//    suspend fun authenticate(
//        @Header("Authorization") authHeader: String?, // <---- Заголовок Authorization, теперь nullable (String?)
//        @Body authRequest: AuthRequest // <---- Тело запроса типа AuthRequest, аннотация @Body
//    ): Response<Void>
}