package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.domain.interactors.GetExternalServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

class GetExternalServerInteractorImpl(private val externalServerRepository: ExternalServerRepository) : GetExternalServerInteractor {

    override suspend fun execute(serverId: Int): Flow<ExternalServer?> { // <---- Реализация метода execute()
        return externalServerRepository.getServer(serverId) // <---- Вызываем метод getServerById() репозитория и возвращаем Flow
    }}