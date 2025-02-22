package com.synngate.twowaysync.ui.screens.remoteserver.edit

data class ExternalServerState(
    val id: Int = -1,
    val serverName: String = "",
    val host: String = "",
    val port: String = "",

    val serverNameError: String = "",//"Имя сервера не может быть пустым",
    val hostError: String = "",//"Хост не может быть пустым",
    val portError: String = "",//"Порт не может быть пустым",
)