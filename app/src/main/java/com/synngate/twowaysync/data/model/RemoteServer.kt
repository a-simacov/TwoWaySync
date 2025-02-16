package com.synngate.twowaysync.data.model

data class RemoteServer(
    val id: Int? = null, //  ID сервера, может быть null для нового сервера
    val serverName: String,
    val host: String,
    val port: Int,
    val authEndpoint: String,
    val echoEndpoint: String,
    val username: String?, //  Имя пользователя и пароль могут быть null
    val password: String?
)