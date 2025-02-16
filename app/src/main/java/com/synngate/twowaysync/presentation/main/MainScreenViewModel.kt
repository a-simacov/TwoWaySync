package com.synngate.twowaysync.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.model.MainScreenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.synngate.twowaysync.data.common.Result

class MainScreenViewModel(
    private val getMainScreenDataInteractor: GetMainScreenDataInteractor
) : ViewModel() {

    // Начальное состояние MainScreenData - пока данные загружаются
    private val initialMainScreenData = MainScreenData(
        logCount = 0,
        remoteServerCount = 0,
        productCount = 0,
        localWebServerStatus = "Загрузка...",
        remoteServerStatus = "Загрузка..."
    )

    // StateFlow для хранения *не-нуллабельных* MainScreenData
    private val _mainScreenDataState = MutableStateFlow(initialMainScreenData) // <---- ИЗМЕНЕНИЕ: StateFlow<MainScreenData>, начальное значение initialMainScreenData
    val mainScreenDataState: StateFlow<MainScreenData> = _mainScreenDataState.asStateFlow()

    // Функция для загрузки данных Главного экрана
    fun loadMainScreenData() {
        viewModelScope.launch { // Используем viewModelScope для корутин в ViewModel
            val result = getMainScreenDataInteractor.invoke()
            when (result) {
                is Result.Success -> {
                    _mainScreenDataState.value = result.data // Обновляем state успешными данными
                }
                is Result.Failure -> {
                    // В случае ошибки, устанавливаем MainScreenData с состоянием ошибки
                    _mainScreenDataState.value = MainScreenData(
                        logCount = 0,
                        remoteServerCount = 0,
                        productCount = 0,
                        localWebServerStatus = "Ошибка загрузки", // Статус ошибки
                        remoteServerStatus = "Ошибка загрузки"    // Статус ошибки
                    )
                    // TODO:  Обработка ошибки загрузки (например, логирование, отображение Snackbar)
                }
            }
        }
    }

    init {
        loadMainScreenData() // Загружаем данные при создании ViewModel
    }
}