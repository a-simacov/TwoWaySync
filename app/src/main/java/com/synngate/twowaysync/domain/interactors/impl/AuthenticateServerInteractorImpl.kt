package com.synngate.twowaysync.domain.interactors.impl // <---- Или ваш пакет для реализаций интеракторов

import com.synngate.twowaysync.domain.interactors.AuthenticateServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthenticateServerInteractorImpl : AuthenticateServerInteractor { // <---- Реализация интерфейса AuthenticateServerInteractor

    override fun execute(serverDetails: ExternalServer): Flow<Boolean> = flow {
//        val baseUrl = "https://${serverDetails.host}:${serverDetails.port}"
//        val apiService = RetrofitClient.getApiService(baseUrl)
//
//        try {
//            val deviceId = "YOUR_DEVICE_ID_HERE" // <---- TODO:  Получить реальный ID устройства
//            val authRequest = AuthRequest(deviceId = deviceId) // <---- Создаем тело запроса AuthRequest
//
//            val response = if (serverDetails.username.isNullOrBlank() || serverDetails.password.isNullOrBlank()) {
//                apiService.echo()
//            } else {
//                val credentials = Credentials.basic(serverDetails.username, serverDetails.password)
//                apiService.echo()
//            }
//
//            if (response.isSuccessful) {
//                emit(true)
//                println("AuthenticateServerInteractorImpl: Аутентификация на сервере ${serverDetails.name} успешна")
//            } else {
//                emit(false)
//                println("AuthenticateServerInteractorImpl: Аутентификация на сервере ${serverDetails.name} не удалась (код: ${response.code()}, сообщение: ${response.message()})")
//            }
//
//        } catch (e: IOException) {
//            emit(false)
//            println("AuthenticateServerInteractorImpl: Ошибка аутентификации на сервере ${serverDetails.name}: ${e.message}")
//            e.printStackTrace()
//        }
    }}