package com.synngate.twowaysync.ui.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.repository.impl.RemoteServerRepositoryImpl
import com.synngate.twowaysync.domain.interactors.GetRemoteServersInteractor
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource // <---- Импорт RemoteServerLocalDataSource
import com.synngate.twowaysync.data.source.local.impl.RemoteServerLocalDataSourceImpl // <---- Импорт RemoteServerLocalDataSourceImpl
import com.synngate.twowaysync.domain.db.AppDatabase
import com.synngate.twowaysync.domain.interactors.impl.GetRemoteServersInteractorImpl


class ChooseRemoteServerScreenViewModelFactory(
    private val context: Context,
    private val navController: NavHostController // <---- NavHostController здесь не нужен, удалим его
) : ViewModelProvider.Factory {

    private val remoteServerDao = AppDatabase.getDatabase(context).remoteServerDao() // <---- Получаем DAO
    private val remoteServerLocalDataSource: RemoteServerLocalDataSource by lazy { // <---- Создаем LocalDataSource
        RemoteServerLocalDataSourceImpl(remoteServerDao) // <---- Передаем DAO в LocalDataSourceImpl
    }


    private val remoteServerRepository: RemoteServerRepository by lazy { // <---- Lazy инициализация репозитория
        RemoteServerRepositoryImpl(remoteServerLocalDataSource) // <---- Передаем LocalDataSource в RepositoryImpl
    }

    private val getRemoteServersInteractor: GetRemoteServersInteractor by lazy { // <---- Lazy инициализация интерактора
        GetRemoteServersInteractorImpl(remoteServerRepository) // <---- Передаем репозиторий в InteractorImpl
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChooseRemoteServerScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChooseRemoteServerScreenViewModel(
                getRemoteServersInteractor = getRemoteServersInteractor,
                navController = navController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}