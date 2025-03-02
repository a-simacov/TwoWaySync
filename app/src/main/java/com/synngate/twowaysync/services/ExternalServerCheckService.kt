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
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.R
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        const val STATUS_CHECK_INTERVAL_MS = 15000L
    }

    private lateinit var actualServerCheckDataStore: ActualServerCheckDataStore
    private var isServiceRunning = false
    private var serverCheckJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        val appDependencies = (application as MyApplication).appDependencies
        val dataStore = appDependencies.dataStore
        actualServerCheckDataStore = ActualServerCheckDataStore(dataStore)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        return START_STICKY
    }

    private fun startForegroundService() {
        if (isServiceRunning) return

        isServiceRunning = true
        serviceScope.launch {
            actualServerCheckDataStore.saveServiceRunningState(true)
        }
        createNotificationChannel()
        val notification = createNotification(SERVICE_NOTIFICATION_CONTENT_RUNNING)
        startForeground(NOTIFICATION_ID, notification)

        serviceScope.launch(Dispatchers.Default) {
            startServerStatusCheck()
        }
    }

    private fun stopForegroundService() {
        if (!isServiceRunning) return

        isServiceRunning = false
        serviceScope.launch {
            actualServerCheckDataStore.saveServiceRunningState(false)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationStopped = createNotification(SERVICE_NOTIFICATION_CONTENT_STOPPED, true)
        notificationManager.notify(NOTIFICATION_ID, notificationStopped)

        stopServerStatusCheck()
        stopForegroundCompat()
        stopSelf()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            SERVICE_CHANNEL_ID,
            "Проверка статуса активного сервера",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
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
        serviceScope.launch {
            delay(1000)
            actualServerCheckDataStore.serviceRunningStateFlow.collectLatest { isRunning ->
                if (isRunning) {
                    if (serverCheckJob?.isActive != true) {
                        serverCheckJob =
                            launchServerCheckTask()
                    }
                } else {
                    stopServerStatusCheck()
                }
            }
        }
    }

    private fun launchServerCheckTask(): Job =
        serviceScope.launch(Dispatchers.IO) {
            while (true) {
                checkServerStatus()
                delay(STATUS_CHECK_INTERVAL_MS)
            }
        }


    private fun stopServerStatusCheck() {
        serverCheckJob?.cancel()
        serverCheckJob = null
        stopForegroundServiceInternal()
    }

    private suspend fun checkServerStatus() {
        val statusInfo = withContext(Dispatchers.IO) {
            val activeApiService = AppDependencies.activeApiService
            if (activeApiService == null)
                "Активный сервер не установлен"
            else {
                try {
                    val response = activeApiService.echo()
                    if (response.isSuccessful)
                        "Сервер доступен (код ${response.code()})"
                    else
                        "Ошибка сервера (код ${response.code()})"
                } catch (e: Exception) {
                    "Ошибка подключения: ${e.message}"
                }
            }
        }

        val currentTime =
            SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        serviceScope.launch {
            actualServerCheckDataStore.saveServerStatus(statusInfo)
            actualServerCheckDataStore.saveServerCheckTime(currentTime)
        }
        updateNotification(statusInfo, currentTime)
        LogHelper.log(statusInfo)
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
        serviceScope.launch {
            actualServerCheckDataStore.saveServiceRunningState(false)
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun isServiceRunning(): Boolean {
        return isServiceRunning
    }
}