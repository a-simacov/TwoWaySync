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

    private var currentServer: ExternalServer = ExternalServer(name = "", host = "", port = 0)

    private val _uiState = MutableStateFlow(ExternalServerUiState())
    val uiState: StateFlow<ExternalServerUiState> = _uiState.asStateFlow()

    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Idle)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val dataStore = MyApplication.appDependencies.dataStore

    fun updateName(name: String) {
        updateUiState { currentState ->
            currentState.copy(
                serverName = name,
                serverNameError = if (name.isBlank()) "Имя сервера не может быть пустым" else ""
            )
        }
    }

    fun updateHost(host: String) {
        updateUiState { currentState ->
            currentState.copy(
                host = host,
                hostError = if (host.isBlank()) "Хост не может быть пустым" else ""
            )
        }
    }

    fun updatePort(port: String) {
        updateUiState { currentState ->
            val portNumber = port.toIntOrNull()
            currentState.copy(
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
                val currentState = _uiState.value

                val serverToSave = ExternalServer(
                    id = currentServer.id,
                    name = currentState.serverName,
                    host = currentState.host,
                    port = currentState.port.toInt()
                )

                val serverId = saveExternalServerInteractor.execute(serverToSave)
                updateUiState { it.copy(id = serverId) }
            }
        }
    }

    private suspend fun isCurrentServerActive(): Boolean {
        val preferences = dataStore.data.first()
        val savedServerId = preferences[CURRENT_SERVER_ID_KEY]
        return savedServerId == currentServer.id
    }

    fun load(serverId: Int) {
        if (serverId == -1) return

        viewModelScope.launch {
            getExternalServerInteractor.execute(serverId).collect { serverDetails ->
                if (serverDetails == null) return@collect
                currentServer = serverDetails.copy()

                val isActive = isCurrentServerActive()

                updateUiState {
                    ExternalServerUiState(
                        id = serverDetails.id!!,
                        serverName = serverDetails.name,
                        host = serverDetails.host,
                        port = serverDetails.port.toString(),
                        isActive = isActive,
                        isActiveText = getActiveStatusText(isActive)
                    )
                }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            deleteExternalServerInteractor.execute(currentServer)
            navController.popBackStack()
        }
    }

    fun toggleActiveStatus() {
        viewModelScope.launch {
            val isActive = isCurrentServerActive()
            updateUiState {
                it.copy(
                    isActive = !isActive,
                    isActiveText = getActiveStatusText(!isActive)
                )
            }

            dataStore.edit { prefs ->
                prefs[CURRENT_SERVER_ID_KEY] = if (isActive) -1 else currentServer.id!!
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _connectionStatus.value = ConnectionStatus.Connecting
            delay(1000)
            withContext(Dispatchers.IO) {
                checkServerAvailabilityInteractor.execute(currentServer)
                    .collect { isAvailable ->
                        _connectionStatus.value = if (isAvailable) {
                            ConnectionStatus.Success
                        } else {
                            ConnectionStatus.Error("Ошибка: Сервер недоступен")
                        }
                    }
            }
        }
    }

    private fun validateForm(): Boolean {
        updateName(_uiState.value.serverName)
        updateHost(_uiState.value.host)
        updatePort(_uiState.value.port)

        return _uiState.value.serverNameError.isEmpty() &&
                _uiState.value.hostError.isEmpty() &&
                _uiState.value.portError.isEmpty()
    }

    private fun updateUiState(update: (ExternalServerUiState) -> ExternalServerUiState) {
        _uiState.value = update(_uiState.value)
    }

    private fun getActiveStatusText(isActive: Boolean): String =
        "Активный: ${if (isActive) "ДА" else "НЕТ"}"
}

sealed class ConnectionStatus(val text: String) {

    override fun toString() = text

    object Idle : ConnectionStatus(text = "Ожидание подключения")

    object Connecting : ConnectionStatus(text = "Подключение...")

    object Success : ConnectionStatus(text = "Сервер доступен")

    data class Error(val message: String) : ConnectionStatus(text = message)
}