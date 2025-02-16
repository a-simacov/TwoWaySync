package com.synngate.twowaysync.domain.model

data class MainScreenData(
    val logCount: Int,
    val remoteServerCount: Int,
    val productCount: Int,
    val localWebServerStatus: String, // или можно использовать enum для статусов
    val remoteServerStatus: String
)