package com.synngate.twowaysync.ui.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.repository.impl.RemoteServerRepositoryImpl
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.RemoteServerLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.AuthenticateServerInteractor
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.interactors.GetRemoteServerDetailsInteractor
import com.synngate.twowaysync.domain.interactors.impl.AuthenticateServerInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl
import com.synngate.twowaysync.domain.interactors.impl.GetRemoteServerDetailsInteractorImpl


class ConnectionScreenViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val remoteServerDao = AppDatabase.getDatabase(context).remoteServerDao()
    private val remoteServerLocalDataSource: RemoteServerLocalDataSource by lazy {
        RemoteServerLocalDataSourceImpl(remoteServerDao)
    }

    private val remoteServerRepository: RemoteServerRepository by lazy {
        RemoteServerRepositoryImpl(remoteServerLocalDataSource)
    }

    private val getRemoteServerDetailsInteractor: GetRemoteServerDetailsInteractor by lazy {
        GetRemoteServerDetailsInteractorImpl(remoteServerRepository)
    }

    private val checkServerAvailabilityInteractor: CheckServerAvailabilityInteractor by lazy {
        CheckServerAvailabilityInteractorImpl()
    }

    private val authenticateServerInteractor: AuthenticateServerInteractor by lazy { // <---- Создание AuthenticateServerInteractor
        AuthenticateServerInteractorImpl() // <---- Создаем AuthenticateServerInteractorImpl
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectionScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConnectionScreenViewModel(
                getRemoteServerDetailsInteractor = getRemoteServerDetailsInteractor,
                checkServerAvailabilityInteractor = checkServerAvailabilityInteractor,
                authenticateServerInteractor = authenticateServerInteractor // <---- Передаем AuthenticateServerInteractor во ViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}