package com.synngate.twowaysync

import android.app.Application
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyApplication : Application() {

    lateinit var appDependencies: AppDependencies

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appDependencies = AppDependencies
        AppDependencies.init(context = this)

        // Синхронная инициализация LogHelper с использованием runBlocking  <----  ИЗМЕНЕНО НА runBlocking
        // todo это плохой способ блокирования, но приходится так делать, т.к. он не успевает
        // инициализироваться, а его уже используют
        applicationScope.launch(Dispatchers.IO) {
            LogHelper.init(appDependencies.provideLogRepository()) // Инициализация LogHelper
            val prefs = appDependencies.dataStore.data.first()
            val activeServerId = prefs[CURRENT_SERVER_ID_KEY] ?: -1
            appDependencies.setCurrentApiService(activeServerId)
        }
    }
}