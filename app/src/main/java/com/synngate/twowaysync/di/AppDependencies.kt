package com.synngate.twowaysync.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.repository.ExternalServerRepository
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.domain.interactors.impl.ProductLocalDataSource
import com.synngate.twowaysync.data.source.local.ExternalServerLocalDataSource
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import com.synngate.twowaysync.domain.service.LocalWebServerService
import com.synngate.twowaysync.ui.screens.main.MainScreenViewModel

interface AppDependencies {

    val dataStore: DataStore<Preferences>

    // Data Source Layer
    fun provideLogLocalDataSource(): LogLocalDataSource
    fun provideRemoteServerLocalDataSource(): ExternalServerLocalDataSource
    fun provideProductLocalDataSource(): ProductLocalDataSource

    // Data Repository Layer
    fun provideLogRepository(): LogRepository
    fun provideRemoteServerRepository(): ExternalServerRepository
    fun provideProductRepository(): ProductRepository

    // Domain Layer - Interactors
    fun provideGetMainScreenDataInteractor(): GetMainScreenDataInteractor

    // Domain Layer - Managers
//    fun provideLocalWebServerManager(): LocalWebServerManager
    fun provideRemoteServerConnectionManager(): RemoteServerConnectionManager

    // Domain Layer - Services
    fun provideLocalWebServerService(): LocalWebServerService

    // Presentation Layer - ViewModels
    fun getMainScreenViewModel(): MainScreenViewModel
}