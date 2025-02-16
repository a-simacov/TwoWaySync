package com.synngate.twowaysync.ui.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.synngate.twowaysync.data.interactors.fake.RealSaveRemoteServerSettingsInteractor
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.repository.impl.RemoteServerRepositoryImpl
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.RemoteServerLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.SaveRemoteServerSettingsInteractor

class RemoteServerScreenViewModelFactory(
    private val context: Context,
    private val navController: NavHostController
) : ViewModelProvider.Factory {

    private val remoteServerDao = AppDatabase.getDatabase(context).remoteServerDao() // <----  Получаем DAO
    private val remoteServerLocalDataSource: RemoteServerLocalDataSource by lazy { // <----  Создаем LocalDataSource
        RemoteServerLocalDataSourceImpl(remoteServerDao) // <----  Передаем DAO в LocalDataSourceImpl
    }

    private val remoteServerRepository: RemoteServerRepository by lazy {
        RemoteServerRepositoryImpl(remoteServerLocalDataSource) // <----  Передаем LocalDataSource в RepositoryImpl
    }

    private val saveRemoteServerSettingsInteractor: SaveRemoteServerSettingsInteractor by lazy {
        RealSaveRemoteServerSettingsInteractor(remoteServerRepository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteServerScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteServerScreenViewModel(
                saveRemoteServerSettingsInteractor = saveRemoteServerSettingsInteractor,
                navController = navController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}