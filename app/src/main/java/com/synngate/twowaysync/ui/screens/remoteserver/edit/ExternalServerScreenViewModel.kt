package com.synngate.twowaysync.ui.screens.remoteserver.edit

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.DeleteExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.GetExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.SaveExternalServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExternalServerScreenViewModel(
    private val getExternalServerInteractor: GetExternalServerInteractor,
    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor,
    private val saveExternalServerInteractor: SaveExternalServerInteractor,
    private val deleteExternalServerInteractor: DeleteExternalServerInteractor,
    private val navController: NavHostController
) : ViewModel() {

    private var server = ExternalServer(name = "", host = "", port = 0)

    private val _state = MutableStateFlow(ExternalServerState())
    val state: StateFlow<ExternalServerState> = _state.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Ожидание подключения")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    private val dataStore = MyApplication.appDependencies.dataStore

    fun updateName(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                serverName = name,
                serverNameError = if (name.isBlank()) "Имя сервера не может быть пустым" else ""
            )
        }
    }

    fun updateHost(host: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                host = host,
                hostError = if (host.isBlank()) "Хост не может быть пустым" else ""
            )
        }
    }

    fun updatePort(port: String) {
        viewModelScope.launch {
            val portNumber = port.toIntOrNull()
            _state.value = _state.value.copy(
                port = port,
                portError = when {
                    port.isBlank() -> "Порт не может быть пустым"
                    portNumber == null -> "Порт должен быть числом"
                    portNumber !in 1..65535 -> "Порт должен быть в диапазоне 1-65535"
                    else -> ""
                }
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            if (validateForm()) {
                val currentState = _state.value

                val server = ExternalServer(
                    id = server.id,
                    name = currentState.serverName,
                    host = currentState.host,
                    port = currentState.port.toInt()
                )

                val serverId = saveExternalServerInteractor.execute(server)
                _state.value = _state.value.copy( id = serverId)
            }
        }
    }

    fun load(serverId: Int) {
        if (serverId == -1) return
        viewModelScope.launch {
            getExternalServerInteractor.execute(serverId).collect { serverDetails ->
                if (serverDetails == null) return@collect
                server = serverDetails.copy()
                _state.value = ExternalServerState(
                    id = serverDetails.id!!,
                    serverName = serverDetails.name,
                    host = serverDetails.host,
                    port = serverDetails.port.toString()
                )
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            deleteExternalServerInteractor.execute(server)
            navController.popBackStack()
        }
    }

    fun setActive() {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            val savedServerId = preferences[CURRENT_SERVER_ID_KEY]
            val currentServerId = server.id!!
            dataStore.edit { preferences ->
                preferences[CURRENT_SERVER_ID_KEY] =
                    if (savedServerId == currentServerId) -1 else currentServerId
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _connectionStatus.value = "Подключение..."
            delay(1000)
            withContext(Dispatchers.IO) {
                checkServerAvailabilityInteractor.execute(server)
                    .collect { isAvailable ->
                        if (isAvailable)
                            _connectionStatus.value = "Сервер доступен."
                        else
                            _connectionStatus.value = "Ошибка: Сервер недоступен"
                    }
            }
        }
    }

    private fun validateForm(): Boolean {
        viewModelScope.launch {
            updateName(_state.value.serverName)
            updateHost(_state.value.host)
            updatePort(_state.value.port)
        }

        return _state.value.serverNameError.isEmpty() &&
                _state.value.hostError.isEmpty() &&
                _state.value.portError.isEmpty()
    }
}