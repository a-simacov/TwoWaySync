package com.synngate.twowaysync.domain.interactors.impl // <---- Или ваш пакет для реализаций интеракторов

import com.synngate.twowaysync.data.network.ApiService
import com.synngate.twowaysync.data.network.RetrofitClient
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CheckServerAvailabilityInteractorImpl : CheckServerAvailabilityInteractor { // <---- Реализация интерфейса CheckServerAvailabilityInteractor

    override fun execute(serverDetails: RemoteServerDetails): Flow<Boolean> = flow { // <---- Реализация метода execute()
        val baseUrl = "https://${serverDetails.host}:${serverDetails.port}" // <---- Формируем базовый URL из хоста и порта
        val apiService = RetrofitClient.getApiService(baseUrl) // <---- Получаем ApiService для базового URL

        try {
            val response = apiService.echo() // <---- Выполняем эхо-запрос
            if (response.isSuccessful) { // <---- Проверяем, что запрос успешен (HTTP статус 200-299)
                emit(true) // <---- Эмитим true, если эхо-запрос успешен
                println("CheckServerAvailabilityInteractorImpl: Сервер ${serverDetails.name} доступен (echo запрос успешен)")
            } else {
                emit(false) // <---- Эмитим false, если эхо-запрос не успешен (не 200 OK)
                println("CheckServerAvailabilityInteractorImpl: Сервер ${serverDetails.name} недоступен (echo запрос не успешен, код: ${response.code()}, сообщение: ${response.message()})")
            }
        } catch (e: IOException) { // <---- Обработка IOException (ошибки сети, таймаут и т.д.)
            emit(false) // <---- Эмитим false, если произошла ошибка сети
            println("CheckServerAvailabilityInteractorImpl: Ошибка проверки доступности сервера ${serverDetails.name}: ${e.message}")
            e.printStackTrace() // <---- Лог ошибки
        }
    }
}