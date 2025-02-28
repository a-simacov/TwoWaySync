package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
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
            val logCountResult = logRepository.getLogsCount()
            val logCount = when (logCountResult) {
                is Result.Success -> logCountResult.data
                is Result.Failure -> 0
            }

            val remoteServerCount = 0

            val productCount = productRepository.getProductsCount()

            val webServerStatus = localWebServerService.getWebServerStatus()
            val remoteServerStatus = remoteServerConnectionManager.getRemoteServerStatus()

            val mainScreenData = MainScreenData(
                logCount = logCount,
                remoteServerCount = remoteServerCount,
                productCount = productCount,
                localWebServerStatus = webServerStatus ?: "Unknown",
                remoteServerStatus = remoteServerStatus ?: "Unknown"
            )
            Result.Success(mainScreenData)

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}