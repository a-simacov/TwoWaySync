package com.synngate.twowaysync.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.domain.interactors.GetRemoteServersInteractor // <---- Импорт GetRemoteServersInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChooseRemoteServerScreenViewModel(
    private val getRemoteServersInteractor: GetRemoteServersInteractor, // <---- Внедрение GetRemoteServersInteractor
    private val navController: NavHostController
) : ViewModel() {

    private val _servers = MutableStateFlow<List<RemoteServerDetails>>(emptyList()) // <---- StateFlow для списка серверов
    val servers: StateFlow<List<RemoteServerDetails>> = _servers.asStateFlow() // <---- Открытый StateFlow для UI

    init {
        loadServers() // <---- Загрузка серверов при инициализации ViewModel
    }

    private fun loadServers() { // <---- Функция загрузки серверов
        viewModelScope.launch {
            getRemoteServersInteractor.execute() // <---- Вызов интерактора для получения Flow<List<RemoteServerDetails>>
                .collect { serverList -> // <---- Подписка на Flow
                    _servers.value = serverList // <---- Обновление StateFlow списком серверов
                    println("ChooseRemoteServerScreenViewModel: Загружено серверов: ${serverList.size}") // Лог
                }
        }
    }

    fun onServerItemClick(serverId: Int) {
        println("ChooseRemoteServerScreenViewModel: Server item clicked, serverId: $serverId")
        navController.navigate("connection_screen/$serverId")
    }

    fun onServerItemLongClick(serverId: Int) {
        println("ChooseRemoteServerScreenViewModel: Server item long clicked, serverId: $serverId")
        navController.navigate("commands_screen/$serverId")
    }
}