package com.synngate.twowaysync.ui.screens.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.model.MainScreenData
import com.synngate.twowaysync.services.ActualServerCheckDataStore
import com.synngate.twowaysync.services.ExternalServerCheckService
import com.synngate.twowaysync.services.LocalServerService
import com.synngate.twowaysync.services.WebServerCheckDataStore
import kotlinx.coroutines.Dispatchers
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
    private val actualServerCheckDataStore: ActualServerCheckDataStore,
    private val webServerCheckDataStore: WebServerCheckDataStore
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

    val iaActualServerCheckIsRunningStateFlow: StateFlow<Boolean> =
        actualServerCheckDataStore.serviceRunningStateFlow.stateInViewModel(false)
    val actualServerStatusFlow: StateFlow<String> =
        actualServerCheckDataStore.serverStatusFlow.stateInViewModel("Ожидание проверки")
    val actualServerCheckTimeFlow: StateFlow<String> =
        actualServerCheckDataStore.serverCheckTimeFlow.stateInViewModel("Нет данных")

    val isWebServerCheckIsRunningStateFlow: StateFlow<Boolean> =
        webServerCheckDataStore.serviceRunningStateFlow.stateInViewModel(false)


    init {
        loadMainScreenData()
    }

    private fun loadMainScreenData() {
        viewModelScope.launch(Dispatchers.IO) {
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
            //webServerCheckDataStore.saveServiceRunningState(false)
        }
    }

    fun startCheckServerService(context: Context) {
        Intent(context, ExternalServerCheckService::class.java).also {
            it.action = ExternalServerCheckService.ACTION_START_FOREGROUND_SERVICE
            context.startForegroundService(it)
        }
    }

    fun stopCheckServerService(context: Context) {
        Intent(context, ExternalServerCheckService::class.java).also {
            it.action = ExternalServerCheckService.ACTION_STOP_FOREGROUND_SERVICE
            context.startForegroundService(it)
        }
    }

    fun startWebServerService(context: Context) {
        Intent(context, LocalServerService::class.java).also {
            it.action = LocalServerService.ACTION_START_FOREGROUND_SERVICE
            context.startForegroundService(it)
        }
    }

    fun stopWebServerService(context: Context) {
        Intent(context, LocalServerService::class.java).also {
            it.action = LocalServerService.ACTION_STOP_FOREGROUND_SERVICE
            context.startForegroundService(it)
        }
    }

    private fun <T> Flow<T>.stateInViewModel(initialValue: T): StateFlow<T> =
        stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialValue
        )
}