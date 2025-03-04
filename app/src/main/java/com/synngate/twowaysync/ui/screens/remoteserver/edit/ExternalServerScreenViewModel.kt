package com.synngate.twowaysync.ui.screens.remoteserver.edit

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.domain.interactors.CheckActiveServerInteractor
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.DeleteExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.GetExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.SaveExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.UpdateActiveServerInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExternalServerScreenViewModel(
    private val getExternalServerInteractor: GetExternalServerInteractor,
    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor,
    private val saveExternalServerInteractor: SaveExternalServerInteractor,
    private val deleteExternalServerInteractor: DeleteExternalServerInteractor,
    private val checkActiveServerInteractor: CheckActiveServerInteractor,
    private val updateActiveServerInteractor: UpdateActiveServerInteractor
) : ViewModel() {

    private var currentServer: ExternalServer = ExternalServer(name = "", host = "", port = 0)

    private val _uiState = MutableStateFlow(ExternalServerUiState())
    val uiState: StateFlow<ExternalServerUiState> = _uiState.asStateFlow()

    private val _events = Channel<ExternalServerEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Idle)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _showDeleteConfirmationDialog = MutableStateFlow(false)
    val showDeleteConfirmationDialog: StateFlow<Boolean> =
        _showDeleteConfirmationDialog.asStateFlow()

    private val _isServerDeleted = MutableStateFlow(false)
    val isServerDeleted: StateFlow<Boolean> = _isServerDeleted.asStateFlow()

    fun onShowDeleteConfirmationDialog() {
        _showDeleteConfirmationDialog.value = true
    }

    fun onDismissDeleteConfirmationDialog() {
        _showDeleteConfirmationDialog.value = false
    }

    fun onDeleteConfirmed() {
        delete()
        onDismissDeleteConfirmationDialog()
    }

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
        viewModelScope.launch(Dispatchers.IO) {
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
                _events.send(ExternalServerEvent.SaveSuccess("Сервер записан успешно"))
            } else {
                _events.send(ExternalServerEvent.Error("Ошибка в заполнении свойств сервера"))
            }
        }
    }

    fun load(serverId: Int) {
        if (serverId == -1) return

        viewModelScope.launch(Dispatchers.IO) {
            getExternalServerInteractor.execute(serverId).collect { externalServer ->
                if (externalServer == null) return@collect
                currentServer = externalServer.copy()

                val isActive = checkActiveServerInteractor.execute(currentServer)

                updateUiState {
                    ExternalServerUiState(
                        id = externalServer.id!!,
                        serverName = externalServer.name,
                        host = externalServer.host,
                        port = externalServer.port.toString(),
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
            _events.send(ExternalServerEvent.DeleteSuccess("Сервер удален успешно"))
        }
    }

    fun serverDeleted() {
        _isServerDeleted.value = true
    }

    fun undoDelete() {
        viewModelScope.launch {
            save()
        }
    }

    fun toggleActiveStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val isActive = checkActiveServerInteractor.execute(currentServer)
            updateUiState {
                it.copy(
                    isActive = !isActive,
                    isActiveText = getActiveStatusText(!isActive)
                )
            }

            updateActiveServerInteractor.execute(currentServer, isActive)
        }
    }

    fun testConnection() {
        viewModelScope.launch(Dispatchers.IO) {
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

interface ExternalServerEvent {

    fun show(context: Context)

    fun showSnackbar(
        context: Context,
        snackbarHostState: SnackbarHostState,
        coroutineScope: CoroutineScope,
        actionLabel: String,
        onActionPerformed: () -> Unit,
        onActionDismissed: () -> Unit
    )

    abstract class Abstract(
        protected val message: String
    ) : ExternalServerEvent {

        override fun show(context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        override fun showSnackbar(
            context: Context,
            snackbarHostState: SnackbarHostState,
            coroutineScope: CoroutineScope,
            actionLabel: String,
            onActionPerformed: () -> Unit,
            onActionDismissed: () -> Unit
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Long
                ).let { result ->
                    if (result == SnackbarResult.ActionPerformed) {
                        onActionPerformed.invoke()
                    } else
                        onActionDismissed.invoke()
                }
            }
        }
    }

    class SaveSuccess(message: String) : Abstract(message)

    class Error(message: String) : Abstract(message)

    class DeleteSuccess(message: String) : Abstract(message)
}

sealed class ConnectionStatus(val text: String) {

    override fun toString() = text

    object Idle : ConnectionStatus(text = "Ожидание подключения")

    object Connecting : ConnectionStatus(text = "Подключение...")

    object Success : ConnectionStatus(text = "Сервер доступен")

    data class Error(val message: String) : ConnectionStatus(text = message)
}