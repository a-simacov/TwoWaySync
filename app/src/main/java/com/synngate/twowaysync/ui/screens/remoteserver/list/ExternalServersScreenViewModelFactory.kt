package com.synngate.twowaysync.ui.screens.remoteserver.list

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.domain.interactors.GetExternalServersInteractor


class ExternalServersScreenViewModelFactory(
    private val getExternalServersInteractor: GetExternalServersInteractor,
    private val dataStore: DataStore<Preferences>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExternalServersScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExternalServersScreenViewModel(
                getExternalServersInteractor = getExternalServersInteractor,
                dataStore = dataStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}