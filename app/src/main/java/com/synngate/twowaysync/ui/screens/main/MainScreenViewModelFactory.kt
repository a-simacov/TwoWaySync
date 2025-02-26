package com.synngate.twowaysync.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl
import com.synngate.twowaysync.services.ServerCheckDataStore

class MainScreenViewModelFactory(
    private val getMainScreenDataInteractor: GetMainScreenDataInteractor,
    private val serverCheckDataStore: ServerCheckDataStore
) : ViewModelProvider.Factory {

    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor by lazy {
        CheckServerAvailabilityInteractorImpl()
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(
                getMainScreenDataInteractor = getMainScreenDataInteractor,
                checkServerAvailabilityInteractor = checkServerAvailabilityInteractor,
                serverCheckDataStore = serverCheckDataStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}