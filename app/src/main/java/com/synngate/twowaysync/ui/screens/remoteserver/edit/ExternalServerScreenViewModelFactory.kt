package com.synngate.twowaysync.ui.screens.remoteserver.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.repository.impl.ExternalServerRepositoryImpl
import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.ExternalServerLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.DeleteExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.GetExternalServerInteractor
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.DeleteExternalServerInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetExternalServerInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.SaveExternalServerInteractorImpl

class ExternalServerScreenViewModelFactory(
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

    private val saveExternalServerInteractor: com.synngate.twowaysync.domain.interactors.SaveExternalServerInteractor by lazy {
        SaveExternalServerInteractorImpl(externalServerRepository)
    }

    private val getExternalServerInteractor: GetExternalServerInteractor by lazy {
        GetExternalServerInteractorImpl(externalServerRepository)
    }

    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor by lazy {
        CheckServerAvailabilityInteractorImpl()
    }

    private val deleteExternalServerInteractor: DeleteExternalServerInteractor by lazy {
        DeleteExternalServerInteractorImpl(externalServerRepository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExternalServerScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExternalServerScreenViewModel(
                getExternalServerInteractor = getExternalServerInteractor,
                checkServerAvailabilityInteractor = checkServerAvailabilityInteractor,
                saveExternalServerInteractor = saveExternalServerInteractor,
                deleteExternalServerInteractor = deleteExternalServerInteractor,
                navController = navController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}