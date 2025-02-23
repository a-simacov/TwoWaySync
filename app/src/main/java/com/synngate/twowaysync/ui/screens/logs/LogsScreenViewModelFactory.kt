package com.synngate.twowaysync.ui.screens.logs

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.impl.LogRepositoryImpl
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.LogLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.interactors.impl.GetLogsInteractorImpl

class LogsScreenViewModelFactory(
    private val context: Context,
    private val navController: NavHostController
) : ViewModelProvider.Factory {

    private val logDao = AppDatabase.getDatabase(context).logDao()
    private val logLocalDataSource: LogLocalDataSource by lazy {
        LogLocalDataSourceImpl(logDao)
    }

    private val logRepository: LogRepository by lazy {
        LogRepositoryImpl(logLocalDataSource)
    }

    private val getLogsInteractor: GetLogsInteractor by lazy {
        GetLogsInteractorImpl(logRepository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogsScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogsScreenViewModel(
                getLogsInteractor = getLogsInteractor,
                navController = navController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}