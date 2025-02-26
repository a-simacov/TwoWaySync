package com.synngate.twowaysync.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.model.MainScreenData
import com.synngate.twowaysync.services.ServerCheckDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val getMainScreenDataInteractor: GetMainScreenDataInteractor,
    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor,
    private val serverCheckDataStore: ServerCheckDataStore
) : ViewModel() {

    private val initialMainScreenData = MainScreenData(
        logCount = 0,
        remoteServerCount = 0,
        productCount = 0,
        localWebServerStatus = "Загрузка...",
        remoteServerStatus = "Загрузка..."
    )

    private val _mainScreenDataState = MutableStateFlow(initialMainScreenData)
    val mainScreenDataState: StateFlow<MainScreenData> = _mainScreenDataState.asStateFlow()

    val serviceRunningStateFlow: StateFlow<Boolean> =
        serverCheckDataStore.serviceRunningStateFlow.stateInViewModel(false)
    val serverStatusFlow: StateFlow<String> =
        serverCheckDataStore.serverStatusFlow.stateInViewModel("Ожидание проверки")
    val serverCheckTimeFlow: StateFlow<String> =
        serverCheckDataStore.serverCheckTimeFlow.stateInViewModel("Нет данных")


    init {
        loadMainScreenData()
    }

    fun loadMainScreenData() {
        viewModelScope.launch {
            val result = getMainScreenDataInteractor.invoke()
            when (result) {
                is Result.Success -> {
                    _mainScreenDataState.value = result.data
                }
                is Result.Failure -> {
                    _mainScreenDataState.value = MainScreenData(
                        logCount = 0,
                        remoteServerCount = 0,
                        productCount = 0,
                        localWebServerStatus = "Ошибка загрузки",
                        remoteServerStatus = "Ошибка загрузки"
                    )
                }
            }
        }
    }

    fun startService(startServiceAction: () -> Unit) {
        startServiceAction()
        viewModelScope.launch {
            serverCheckDataStore.saveServiceRunningState(true)
        }
    }

    fun stopService(stopServiceAction: () -> Unit) {
        stopServiceAction()
        viewModelScope.launch {
            serverCheckDataStore.saveServiceRunningState(false)
        }
    }

    private fun <T> Flow<T>.stateInViewModel(initialValue: T): StateFlow<T> =
        stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialValue
        )
}