package com.synngate.twowaysync.data.interactors.fake

import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import com.synngate.twowaysync.domain.interactors.SaveRemoteServerSettingsInteractor
import kotlinx.coroutines.delay

class RealSaveRemoteServerSettingsInteractor(
    private val remoteServerRepository: RemoteServerRepository
) : SaveRemoteServerSettingsInteractor {

    override suspend fun execute(remoteServer: RemoteServerDetails) {
        delay(1000) // Имитация задержки (оставляем пока для реалистичности)
        remoteServerRepository.insert(remoteServer) // Используем метод insert() репозитория для сохранения
        println("RealSaveRemoteServerSettingsInteractor: Сохранение настроек сервера в Room Database: ${remoteServer.name}") // Лог
    }
}