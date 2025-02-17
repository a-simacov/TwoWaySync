package com.synngate.twowaysync.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.domain.interactors.AuthenticateServerInteractor
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetRemoteServerDetailsInteractor
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionScreenViewModel(
    private val getRemoteServerDetailsInteractor: GetRemoteServerDetailsInteractor, // <---- Внедрение GetRemoteServerByIdInteractor
    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor, // <---- Внедрение CheckServerAvailabilityInteractor
    private val authenticateServerInteractor: AuthenticateServerInteractor
) : ViewModel() {

    private val _server = MutableStateFlow<RemoteServerDetails?>(null) // <---- StateFlow для хранения деталей сервера
    val server: StateFlow<RemoteServerDetails?> = _server.asStateFlow() // <---- Открытый StateFlow для UI

    private val _connectionStatus = MutableStateFlow<String>("Ожидание подключения") // <---- StateFlow для статуса подключения
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow() // <---- Открытый StateFlow для UI


    fun loadServerDetails(serverId: Int) { // <---- Функция загрузки деталей сервера по ID
        viewModelScope.launch {
            getRemoteServerDetailsInteractor.execute(serverId).collect { serverDetails ->// <---- Получаем детали сервера через интерактор
                _server.value = serverDetails // <---- Обновляем StateFlow с деталями сервера
                println("ConnectionScreenViewModel: Загружены детали сервера: ${serverDetails?.name}, ID: $serverId") // Лог
            }
        }
    }

    fun connectToServer() {
        viewModelScope.launch {
            _connectionStatus.value = "Подключение..."

            val serverDetails = _server.value
            if (serverDetails == null) {
                _connectionStatus.value = "Ошибка: Детали сервера не загружены"
                println("ConnectionScreenViewModel: Ошибка подключения: Детали сервера не загружены")
                return@launch
            }

            withContext(Dispatchers.IO) {
                checkServerAvailabilityInteractor.execute(serverDetails)
                    .collect { isAvailable ->
                        if (isAvailable) {
                            _connectionStatus.value = "Сервер доступен. Аутентификация..." // <---- Обновленный статус

                            authenticateServerInteractor.execute(serverDetails) // <---- Вызываем AuthenticateServerInteractor
                                .collect { isAuthenticated -> // <---- Собираем результат аутентификации
                                    if (isAuthenticated) {
                                        _connectionStatus.value = "Подключено успешно" // <---- Обновляем статус на "Подключено успешно" после аутентификации
                                        println("ConnectionScreenViewModel: Аутентификация успешна. Подключение установлено.")
                                        // TODO: Сохранить, что сервер является "текущим" (если нужно)
                                    } else {
                                        _connectionStatus.value = "Ошибка: Аутентификация не удалась" // <---- Обновляем статус на "Ошибка: Аутентификация не удалась"
                                        println("ConnectionScreenViewModel: Аутентификация не удалась.")
                                    }
                                }


                        } else {
                            _connectionStatus.value = "Ошибка: Сервер недоступен"
                            println("ConnectionScreenViewModel: Сервер недоступен (проверка доступности не удалась)")
                        }
                    }
            }
        }
    }
    // TODO: Добавить функции для обработки ошибок подключения, отключения, и т.д.
}