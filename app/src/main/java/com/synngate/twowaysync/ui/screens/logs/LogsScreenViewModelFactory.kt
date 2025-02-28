package com.synngate.twowaysync.ui.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.di.LogsScreensDpenedencies

class LogsScreenViewModelFactory(
    private val logScreenDependencies: LogsScreensDpenedencies
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogsScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return with(logScreenDependencies) {
                LogsScreenViewModel(
                    getLogsInteractor = getLogsInteractor,
                    deleteLogsInteractor = deleteLogsInteractor,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}