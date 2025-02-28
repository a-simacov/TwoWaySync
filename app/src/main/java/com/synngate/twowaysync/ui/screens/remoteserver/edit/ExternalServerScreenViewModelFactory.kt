package com.synngate.twowaysync.ui.screens.remoteserver.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.di.ServersGraphDependencies

class ExternalServerScreenViewModelFactory(
    private val serversGraphDependencies: ServersGraphDependencies,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExternalServerScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return with(serversGraphDependencies)
            {
                ExternalServerScreenViewModel(
                    getExternalServerInteractor = getExternalServerInteractor,
                    checkServerAvailabilityInteractor = checkServerAvailabilityInteractor,
                    saveExternalServerInteractor = saveExternalServerInteractor,
                    deleteExternalServerInteractor = deleteExternalServerInteractor,
                    checkActiveServerInteractor = checkActiveServerInteractor,
                    updateActiveServerInteractor = updateActiveServerInteractor
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}