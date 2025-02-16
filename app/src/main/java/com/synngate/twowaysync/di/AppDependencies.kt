package com.synngate.twowaysync.di

import android.content.Context
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.data.repository.ProductRepository
import com.synngate.twowaysync.data.repository.RemoteServerRepository
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.data.source.local.ProductLocalDataSource
import com.synngate.twowaysync.data.source.local.RemoteServerLocalDataSource
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.manager.RemoteServerConnectionManager
import com.synngate.twowaysync.domain.service.LocalWebServerService
import com.synngate.twowaysync.presentation.main.MainScreenViewModel
import com.synngate.twowaysync.util.LogHelper

interface AppDependencies {
    // Data Source Layer
    fun provideLogLocalDataSource(): LogLocalDataSource
    fun provideRemoteServerLocalDataSource(): RemoteServerLocalDataSource
    fun provideProductLocalDataSource(): ProductLocalDataSource

    // Data Repository Layer
    fun provideLogRepository(): LogRepository
    fun provideRemoteServerRepository(): RemoteServerRepository
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