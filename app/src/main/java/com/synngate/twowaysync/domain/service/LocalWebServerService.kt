package com.synngate.twowaysync.domain.service

interface LocalWebServerService {
    fun startWebServer() // Запускает локальный веб-сервер
    fun stopWebServer() // Останавливает локальный веб-сервер
    fun getWebServerStatus(): String? // Возвращает статус веб-сервера (например, "Running", "Stopped", "Error")
    // В будущем можно добавить другие методы для управления веб-сервером или получения информации о нем
}