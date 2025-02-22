package com.synngate.twowaysync

import android.app.Application
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.di.impl.AppDependenciesImpl
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.runBlocking

class MyApplication : Application() {

    companion object {
        lateinit var appDependencies: AppDependencies
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appDependencies = AppDependenciesImpl(this)

        // Синхронная инициализация LogHelper с использованием runBlocking  <----  ИЗМЕНЕНО НА runBlocking
        // todo это плохой способ блокирования, но приходится так делать, т.к. он не успевает
        // инициализироваться, а его уже используют
        runBlocking {
            LogHelper.init(appDependencies.provideLogRepository()) // Инициализация LogHelper
        }
    }
}