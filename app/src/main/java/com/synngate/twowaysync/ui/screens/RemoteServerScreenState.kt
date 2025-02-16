package com.synngate.twowaysync.ui.screens

data class RemoteServerScreenState(
    val serverName: String = "",
    val host: String = "",
    val port: String = "",
    val authEndpoint: String = "/auth",
    val echoEndpoint: String = "/echo",
    val username: String = "",
    val password: String = "",

    //  Свойства для хранения сообщений об ошибках валидации (теперь String, а не String?)
    val serverNameError: String = "", // <----  Ошибка для имени сервера, по умолчанию "" (нет ошибки)
    val hostError: String = "",        // <----  Ошибка для хоста, по умолчанию "" (нет ошибки)
    val portError: String = "",        // <----  Ошибка для порта, по умолчанию "" (нет ошибки)
    val passwordError: String = ""     // <----  Ошибка для пароля, по умолчанию "" (нет ошибки)
)