package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import com.synngate.twowaysync.domain.model.MainScreenData
import com.synngate.twowaysync.domain.service.LocalWebServerService

class GetMainScreenDataInteractorImpl(
    private val logRepository: LogRepository,
    private val externalServerRepository: ExternalServerRepository,
    private val productRepository: ProductRepository,
    private val remoteServerConnectionManager: RemoteServerConnectionManager,
    private val localWebServerService: LocalWebServerService
) : GetMainScreenDataInteractor {

    override suspend fun invoke(): Result<MainScreenData> {
        return try {
            // Получаем количество логов
            val logCountResult = logRepository.getLogsCount()
            val logCount = when (logCountResult) {
                is Result.Success -> logCountResult.data // Используем data.size для получения количества
                is Result.Failure -> 0 // В случае ошибки получения логов, считаем количество 0
            }

            // Получаем количество удаленных серверов
            val remoteServerCountResult = externalServerRepository.getAllServers()
            val remoteServerCount = 0 //when (remoteServerCountResult) {
//                is Result.Success -> remoteServerCountResult.data.size // Используем data.size для получения количества
//                is Result.Failure -> 0 // В случае ошибки получения серверов, считаем количество 0
//            }

            // Получаем количество товаров
            val productCountResult = productRepository.getProducts(null) // filter = null, чтобы получить все товары
            val productCount = when (productCountResult) {
                is Result.Success -> productCountResult.data.size // Используем data.size для получения количества
                is Result.Failure -> 0 // В случае ошибки получения товаров, считаем количество 0
            }

            // Получаем статус локального веб-сервера
            val webServerStatus = localWebServerService.getWebServerStatus() // Получаем статус веб-сервера
            val remoteServerStatus = remoteServerConnectionManager.getRemoteServerStatus() // Получаем статус удаленного сервера

            val mainScreenData = MainScreenData(
                logCount = logCount, // Преобразуем Int в Long, если необходимо
                remoteServerCount = remoteServerCount, // Преобразуем Int в Long, если необходимо
                productCount = productCount, // Преобразуем Int в Long, если необходимо
                localWebServerStatus = webServerStatus ?: "Unknown", // Статус веб-сервера, "Unknown" по умолчанию
                remoteServerStatus = remoteServerStatus ?: "Unknown" // Статус удаленного сервера, "Unknown" по умолчанию
            )
            Result.Success(mainScreenData) // Возвращаем Result.Success с MainScreenData

        } catch (e: Exception) {
            Result.Failure(e) // Возвращаем Result.Failure в случае ошибки
        }
    }
}