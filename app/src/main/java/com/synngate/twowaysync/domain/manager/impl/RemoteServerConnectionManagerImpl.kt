package com.synngate.twowaysync.domain.manager.impl

import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class RemoteServerConnectionManagerImpl : RemoteServerConnectionManager {

    override fun checkServerAvailability(host: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(InetAddress.getByName(host), port), 2000) // Timeout 2 секунды
            socket.close()
            true // Соединение установлено успешно, сервер доступен
        } catch (e: Exception) {
            false // Ошибка соединения, сервер недоступен
        }
    }

    override fun getRemoteServerStatus(): String { // <----  ДОБАВЛЯЕМ реализацию метода getRemoteServerStatus()
        // TODO:  Реализовать получение реального статуса удаленного сервера
        return "Неизвестно" //  Временно возвращаем "Неизвестно" в качестве заглушки
    }
}