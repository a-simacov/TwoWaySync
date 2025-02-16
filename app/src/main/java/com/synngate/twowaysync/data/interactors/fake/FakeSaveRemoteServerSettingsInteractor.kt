package com.synngate.twowaysync.data.interactors.fake

import com.synngate.twowaysync.data.model.RemoteServer
import com.synngate.twowaysync.domain.interactors.SaveRemoteServerSettingsInteractor
import kotlinx.coroutines.delay

class FakeSaveRemoteServerSettingsInteractor : SaveRemoteServerSettingsInteractor { // <----  Заглушка интерактора

    override suspend fun execute(remoteServer: RemoteServer) { // <----  Реализация метода execute
        delay(1000) // <----  Имитация задержки сохранения (1 секунда)
        println("FakeSaveRemoteServerSettingsInteractor: Сохранение настроек сервера: $remoteServer") //  Выводим сообщение в Logcat
        //  TODO:  В дальнейшем здесь будет реальная логика сохранения данных (например, в базу данных)
    }
}