package com.synngate.twowaysync.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.R
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.domain.interactors.impl.network.RetrofitClient
import com.synngate.twowaysync.ui.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExternalServerCheckService : Service() {

    companion object {
        const val SERVICE_CHANNEL_ID = "ServerCheckChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val STATUS_CHECK_INTERVAL_MS = 10000L
    }

    private lateinit var serverCheckDataStore: ServerCheckDataStore
    private lateinit var dataStore: DataStore<Preferences>
    private var serviceIsRunning: Boolean = false

    override fun onCreate() {
        super.onCreate()
        val appDependencies = (application as MyApplication).appDependencies
        dataStore = appDependencies.dataStore
        serverCheckDataStore = ServerCheckDataStore(dataStore)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                startForegroundService()
            }

            ACTION_STOP_SERVICE -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(true) // Сохраняем состояние сервиса в DataStore
        }
        createNotificationChannel()
        startForeground(
            NOTIFICATION_ID,
            createNotification("Сервис запущен", "Ожидание первой проверки")
        )
        startServerStatusCheck()
        serviceIsRunning = true
    }

    private fun stopForegroundService() {
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(false) // Сохраняем состояние сервиса в DataStore
        }
        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Сервис проверки сервера",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(title: String, message: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.outline_confirmation_number_24)
            .setContentIntent(pendingIntent)
            .build()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startServerStatusCheck() {
        GlobalScope.launch(Dispatchers.IO) {
            while (serviceIsRunning) { // Сервис работает постоянно, состояние управляется через DataStore
//                val isServiceRunning = runBlocking { // Используем runBlocking для получения значения из Flow синхронно
//                    serverCheckDataStore.serviceRunningStateFlow.first() // Получаем текущее значение из Flow
//                }
//                if (!isServiceRunning) { // Проверяем текущее состояние сервиса
//                    break // Выходим из цикла, если сервис остановлен
//                }
                checkServerStatus()
                delay(STATUS_CHECK_INTERVAL_MS)
            }
            stopForegroundServiceInternal() // Останавливаем сервис после выхода из цикла
        }
    }

    private suspend fun checkServerStatus() {
        val statusInfo = withContext(Dispatchers.IO) {
            val prefs = dataStore.data.first()
            val activeServerId = prefs[CURRENT_SERVER_ID_KEY] ?: -1
            val activeServer =
                (application as MyApplication).appDependencies.provideRemoteServerRepository()
                    .getServer(activeServerId).first()

            if (activeServer == null) {
                "Активный сервер не установлен"
            } else {
                val baseUrl = "https://${activeServer.host}:${activeServer.port}"
                val apiService = RetrofitClient.getApiService(baseUrl)

                try {
                    val response = apiService.echo()
                    if (response.isSuccessful)
                        "Сервер доступен (код ${response.code()})"
                    else
                        "Ошибка сервера (код ${response.code()})"
                } catch (e: IOException) {
                    "Ошибка подключения: ${e.message}"
                }
            }
        }

        val currentTime =
            SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        GlobalScope.launch {
            serverCheckDataStore.saveServerStatus(statusInfo) // Сохраняем статус в DataStore
            serverCheckDataStore.saveServerCheckTime(currentTime) // Сохраняем время в DataStore
        }
        updateNotification(statusInfo, currentTime)
    }

    private fun updateNotification(status: String, time: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(
            "Состояние сервера: $status",
            "Последняя проверка: $time"
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundServiceInternal() { // Внутренний метод остановки
        serviceIsRunning = false
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(false) // Убеждаемся, что состояние остановлено при onDestroy
        }
        super.onDestroy()
    }
}