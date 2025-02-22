package com.synngate.twowaysync.ui.screens.remoteserver.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.repository.impl.ExternalServerRepositoryImpl
import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.ExternalServerLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.GetExternalServersInteractor
import com.synngate.twowaysync.domain.interactors.impl.GetExternalServersInteractorImpl


class ExternalServersScreenViewModelFactory(
    private val context: Context,
    private val navController: NavHostController
) : ViewModelProvider.Factory {

    private val externalServerDao = AppDatabase.getDatabase(context).externalServerDao()
    private val externalServerLocalDataSource: ExternalServerLocalDataSource by lazy {
        ExternalServerLocalDataSourceImpl(externalServerDao)
    }

    private val externalServerRepository: ExternalServerRepository by lazy {
        ExternalServerRepositoryImpl(externalServerLocalDataSource)
    }

    private val getExternalServersInteractor: GetExternalServersInteractor by lazy {
        GetExternalServersInteractorImpl(externalServerRepository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExternalServersScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExternalServersScreenViewModel(
                getExternalServersInteractor = getExternalServersInteractor,
                navController = navController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}