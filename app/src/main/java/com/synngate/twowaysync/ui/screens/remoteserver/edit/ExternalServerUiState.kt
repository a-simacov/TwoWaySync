package com.synngate.twowaysync.ui.screens.remoteserver.edit

data class ExternalServerUiState(
    val id: Int = -1,
    val serverName: String = "",
    val host: String = "",
    val port: String = "",
    val isActive: Boolean = false,
    val isActiveText: String = "Активный: НЕТ",
    val serverNameError: String = "",
    val hostError: String = "",
    val portError: String = ""
)