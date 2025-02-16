package com.synngate.twowaysync.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController // <---- Импорт NavHostController
import com.synngate.twowaysync.domain.interactors.SaveRemoteServerSettingsInteractor

class RemoteServerScreenViewModelFactory(
    private val saveRemoteServerSettingsInteractor: SaveRemoteServerSettingsInteractor,
    private val navController: NavHostController // <---- Добавляем NavHostController в конструктор фабрики
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteServerScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteServerScreenViewModel(
                saveRemoteServerSettingsInteractor = saveRemoteServerSettingsInteractor,
                navController = navController // <---- Передаем NavHostController во ViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}