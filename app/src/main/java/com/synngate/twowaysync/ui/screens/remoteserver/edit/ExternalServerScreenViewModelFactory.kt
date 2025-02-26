package com.synngate.twowaysync.ui.screens.remoteserver.edit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.di.ServersGraphDependencies

class ExternalServerScreenViewModelFactory(
    private val serversGraphDependencies: ServersGraphDependencies,
    private val dataStore: DataStore<Preferences>
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
                    dataStore = dataStore
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}