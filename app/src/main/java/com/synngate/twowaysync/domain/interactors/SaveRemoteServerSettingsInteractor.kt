package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.model.RemoteServer

interface SaveRemoteServerSettingsInteractor { // <----  Интерфейс интерактора сохранения настроек

    suspend fun execute(remoteServer: RemoteServer) // <----  Метод execute принимает RemoteServer
}