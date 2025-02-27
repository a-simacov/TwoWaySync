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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.R
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.domain.interactors.impl.network.RetrofitClient
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
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
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_STOP_SERVICE_FROM_NOTIFICATION = "ACTION_STOP_SERVICE_FROM_NOTIFICATION"
        const val SERVICE_NOTIFICATION_TITLE = "Сервис проверки сервера"
        const val SERVICE_NOTIFICATION_CONTENT_RUNNING = "Сервис запущен и проверяет сервер..."
        const val SERVICE_NOTIFICATION_CONTENT_STOPPED = "Сервис остановлен."
        const val STATUS_CHECK_INTERVAL_MS = 30000L
    }

    private lateinit var serverCheckDataStore: ServerCheckDataStore
    private lateinit var dataStore: DataStore<Preferences>
    private var isServiceRunning = false
    private var serverCheckJob: Job? = null // Job для управления корутиной проверки сервера
    private val serviceScope = CoroutineScope(Dispatchers.Default) // Скоуп для корутин сервиса

    override fun onCreate() {
        super.onCreate()
        val appDependencies = (application as MyApplication).appDependencies
        dataStore = appDependencies.dataStore
        serverCheckDataStore = ServerCheckDataStore(dataStore)
        Log.d("slax", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("slax", "onStartCommand - begin")
        when (intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                startForegroundService()
            }

            ACTION_STOP_FOREGROUND_SERVICE, ACTION_STOP_SERVICE_FROM_NOTIFICATION -> {
                stopForegroundService()
            }

            else -> {
                // Обработка других действий, если необходимо
            }
        }
        Log.d("slax", "onStartCommand - end")
        return START_STICKY
    }

    private fun startForegroundService() {
        Log.d("slax", "startForegroundService - begin")
        if (isServiceRunning) return

        isServiceRunning = true
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(true)
        }
        createNotificationChannel()
        val notification = createNotification(SERVICE_NOTIFICATION_CONTENT_RUNNING)
        startForeground(NOTIFICATION_ID, notification)

        // Явно запускаем startServerStatusCheck() в корутине на Dispatchers.Default
        serviceScope.launch(Dispatchers.Default) {
            startServerStatusCheck()
        }
        Log.d("slax", "startForegroundService - end")
    }

    private fun stopForegroundService() {
        if (!isServiceRunning) return

        isServiceRunning = false
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(false)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationStopped = createNotification(SERVICE_NOTIFICATION_CONTENT_STOPPED, true)
        notificationManager.notify(NOTIFICATION_ID, notificationStopped)

        stopServerStatusCheck() // Останавливаем корутину проверки сервера реактивно
        stopForegroundCompat()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Проверка статуса активного сервера",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(contentText: String, canDismiss: Boolean = false): Notification {
        val stopIntent = Intent(this, ExternalServerCheckService::class.java).apply {
            action = ACTION_STOP_SERVICE_FROM_NOTIFICATION
        }
        val stopPendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val builder = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle(SERVICE_NOTIFICATION_TITLE)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(!canDismiss)
            .addAction(R.drawable.ic_stop, "Остановить", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (canDismiss) {
            builder.setAutoCancel(true)
        }

        return builder.build()
    }

    private fun startServerStatusCheck() {
        serviceScope.launch { // Запускаем корутину в serviceScope
            delay(1000)
            serverCheckDataStore.serviceRunningStateFlow.collectLatest { isRunning -> // Используем collectLatest для реактивного наблюдения
                if (isRunning) {
                    if (serverCheckJob?.isActive != true) { // Проверяем, что задача еще не запущена или не активна
                        serverCheckJob =
                            launchServerCheckTask() // Запускаем задачу проверки сервера
                    }
                } else {
                    stopServerStatusCheck() // Останавливаем задачу проверки сервера, если сервис остановлен
                }
            }
        }
    }

    private fun launchServerCheckTask(): Job =
        serviceScope.launch(Dispatchers.IO) { // Функция для запуска задачи проверки сервера
            while (true) {
                checkServerStatus()
                delay(STATUS_CHECK_INTERVAL_MS)
            }
        }


    private fun stopServerStatusCheck() {
        serverCheckJob?.cancel() // Отменяем корутину проверки сервера
        serverCheckJob = null // Обнуляем Job
        stopForegroundServiceInternal() // Внутренняя остановка сервиса (теперь вызывается реактивно из collectLatest при остановке сервиса)
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
                    val connectionError = "Ошибка подключения: ${e.message}"
                    LogHelper.log(connectionError, "ERROR")
                    connectionError
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
            "$status\nПоследняя проверка: $time"
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundServiceInternal() {
        stopForegroundCompat()
        stopSelf()
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        isServiceRunning = false
        serviceScope.cancel() // Отменяем serviceScope и все запущенные в нем корутины при onDestroy
        GlobalScope.launch {
            serverCheckDataStore.saveServiceRunningState(false)
        }
        super.onDestroy()
    }

    private fun isServiceRunning(): Boolean {
        return isServiceRunning
    }
}