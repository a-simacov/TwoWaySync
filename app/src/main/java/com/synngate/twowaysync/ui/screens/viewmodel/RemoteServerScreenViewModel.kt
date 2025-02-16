package com.synngate.twowaysync.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.data.model.RemoteServer
import com.synngate.twowaysync.domain.interactors.SaveRemoteServerSettingsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.synngate.twowaysync.ui.screens.RemoteServerScreenState

class RemoteServerScreenViewModel(
    private val saveRemoteServerSettingsInteractor: SaveRemoteServerSettingsInteractor, // <----  Интерактор как зависимость
    private val navController: NavHostController
) : ViewModel() {

    private val _state = MutableStateFlow(RemoteServerScreenState())
    val state: StateFlow<RemoteServerScreenState> = _state.asStateFlow()

    //  Метод для обновления имени сервера и ВАЛИДАЦИЯ
    fun updateServerName(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                serverName = name,
                serverNameError = if (name.isBlank()) "Имя сервера не может быть пустым" else "" // <----  Валидация: "" вместо null при отсутствии ошибки
            )
        }
    }

    //  Метод для обновления хоста и ВАЛИДАЦИЯ
    fun updateHost(host: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                host = host,
                hostError = if (host.isBlank()) "Хост не может быть пустым" else "" // <----  Валидация: "" вместо null при отсутствии ошибки
            )
        }
    }

    //  Метод для обновления порта и ВАЛИДАЦИЯ
    fun updatePort(port: String) {
        viewModelScope.launch {
            val portNumber = port.toIntOrNull()
            _state.value = _state.value.copy(
                port = port,
                portError = when {
                    port.isBlank() -> "Порт не может быть пустым"
                    portNumber == null -> "Порт должен быть числом"
                    portNumber !in 1..65535 -> "Порт должен быть в диапазоне 1-65535"
                    else -> "" // <----  Валидация: "" вместо null при отсутствии ошибки
                }
            )
        }
    }

    //  Методы updateAuthEndpoint, updateEchoEndpoint, updateUsername, updatePassword - без изменений

    fun updateAuthEndpoint(authEndpoint: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(authEndpoint = authEndpoint)
        }
    }

    fun updateEchoEndpoint(echoEndpoint: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(echoEndpoint = echoEndpoint)
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(username = username)
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                password = password,
                passwordError = if (state.value.username.isNotBlank() && password.length < 6) "Пароль должен быть не менее 6 символов, если указано имя пользователя" else "" // <---- Валидация: "" вместо null при отсутствии ошибки
            )
        }
    }


    //  Метод для ВАЛИДАЦИИ ВСЕЙ ФОРМЫ
    fun validateForm(): Boolean {
        viewModelScope.launch {
            updateServerName(_state.value.serverName)
            updateHost(_state.value.host)
            updatePort(_state.value.port)
            updatePassword(_state.value.password)

            //  Проверяем, есть ли ОШИБКИ - теперь проверяем на Пустую строку ""
            val hasErrors = _state.value.serverNameError.isNotEmpty() || // <----  Проверка на .isNotEmpty() вместо != null
                    _state.value.hostError.isNotEmpty() ||          // <----  Проверка на .isNotEmpty() вместо != null
                    _state.value.portError.isNotEmpty() ||          // <----  Проверка на .isNotEmpty() вместо != null
                    _state.value.passwordError.isNotEmpty()         // <----  Проверка на .isNotEmpty() вместо != null

            if (!hasErrors) {
                println("RemoteServerScreenViewModel: Форма валидна")
            } else {
                println("RemoteServerScreenViewModel: Форма не валидна, есть ошибки: ${_state.value}")
            }
        }

        //  Возвращаем true, если ошибок НЕТ (т.е. все Error свойства - Пустые строки ""), false - если ошибки ЕСТЬ (хотя бы одно Error свойство - Не пустая строка)
        return _state.value.serverNameError.isEmpty() && // <----  Проверка на .isEmpty() вместо == null
                _state.value.hostError.isEmpty() &&           // <----  Проверка на .isEmpty() вместо == null
                _state.value.portError.isEmpty() &&           // <----  Проверка на .isEmpty() вместо == null
                _state.value.passwordError.isEmpty()          // <----  Проверка на .isEmpty() вместо == null
    }


    //  Метод для кнопки "Сохранить" - ТЕПЕРЬ ИСПОЛЬЗУЕМ ИНТЕРАКТОР и НАВИГАЦИЮ
    fun saveServerSettings() {
        viewModelScope.launch {
            if (validateForm()) { //  Валидируем форму перед сохранением
                val currentState = _state.value //  Получаем текущее состояние UI

                val remoteServer = RemoteServer( //  Создаем объект RemoteServer из состояния UI
                    serverName = currentState.serverName,
                    host = currentState.host,
                    port = currentState.port.toInt(), // Преобразуем Port в Int
                    authEndpoint = currentState.authEndpoint,
                    echoEndpoint = currentState.echoEndpoint,
                    username = currentState.username.takeIf { it.isNotBlank() }, //  username и password - только если не пустые
                    password = currentState.password.takeIf { it.isNotBlank() }  //  .takeIf { it.isNotBlank() } вернет null, если строка пустая
                )

                saveRemoteServerSettingsInteractor.execute(remoteServer) // <----  Вызываем интерактор для сохранения

                println("RemoteServerScreenViewModel: Настройки сервера сохранены, навигация назад") //  Сообщение в Logcat

                navController.popBackStack() // <----  Навигация назад на ChooseRemoteServerScreen
            } else {
                println("RemoteServerScreenViewModel: Форма НЕ валидна, сохранение отменено. Состояние: ${_state.value}") // Ошибки уже отображаются в UI
            }
        }
    }

    //  Метод-заглушка для кнопки "Test connection" - без изменений
    fun testConnection() {
        viewModelScope.launch {
            println("RemoteServerScreenViewModel: Кнопка 'Test connection' нажата. Состояние: ${_state.value}")
            // TODO:  Реализовать логику тестирования соединения (навигация на RemoteServerConnectionScreen)
        }
    }
}