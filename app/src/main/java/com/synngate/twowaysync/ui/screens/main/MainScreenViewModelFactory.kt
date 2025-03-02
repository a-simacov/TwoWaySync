package com.synngate.twowaysync.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.di.MainScreenDependencies

class MainScreenViewModelFactory(
    private val mainScreenDependencies: MainScreenDependencies,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return with(mainScreenDependencies) {
                MainScreenViewModel(
                    getMainScreenDataInteractor = getMainScreenDataInteractor,
                    checkServerAvailabilityInteractor = checkServerAvailabilityInteractor,
                    actualServerCheckDataStore = actualServerCheckDataStore,
                    webServerCheckDataStore = webServerCheckDataStore
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}