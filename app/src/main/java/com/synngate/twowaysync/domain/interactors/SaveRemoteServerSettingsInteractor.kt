package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.domain.model.RemoteServerDetails

interface SaveRemoteServerSettingsInteractor { // <----  Интерфейс интерактора сохранения настроек

    suspend fun execute(remoteServer: RemoteServerDetails) // <----  Метод execute принимает RemoteServer
}