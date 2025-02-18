package com.synngate.twowaysync.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.domain.interactors.GetRemoteServersInteractor // <---- Импорт GetRemoteServersInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChooseRemoteServerScreenViewModel(
    private val getRemoteServersInteractor: GetRemoteServersInteractor, // <---- Внедрение GetRemoteServersInteractor
    private val navController: NavHostController
) : ViewModel() {

    private val _servers = MutableStateFlow<List<RemoteServerDetails>>(emptyList()) // <---- StateFlow для списка серверов
    val servers: StateFlow<List<RemoteServerDetails>> = _servers.asStateFlow() // <---- Открытый StateFlow для UI

    private val dataStore = MyApplication.appDependencies.dataStore // <---- Получаем DataStore статически в ViewModel

    val savedServerIdState: StateFlow<Int?> = dataStore.data
        .map { preferences ->
            preferences[CURRENT_SERVER_ID_KEY]
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // <----  Подписка активна, пока есть подписчики
            initialValue = null // <----  Начальное значение null
        )

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