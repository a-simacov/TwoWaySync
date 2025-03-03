package com.synngate.twowaysync.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.R
import com.synngate.twowaysync.data.source.remote.KtorDeviceServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LocalServerService : Service() {

    companion object {
        const val SERVICE_CHANNEL_ID = "LocalServerChannel"
        const val NOTIFICATION_ID = 2
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_STOP_SERVICE_FROM_NOTIFICATION = "ACTION_STOP_SERVICE_FROM_NOTIFICATION"
        const val SERVICE_NOTIFICATION_TITLE = "Локальный веб-сервер"
        const val SERVICE_NOTIFICATION_CONTENT_RUNNING = "Сервер запущен..."
        const val SERVICE_NOTIFICATION_CONTENT_STOPPED = "Сервер остановлен."
        const val ACTION_RESTART_SERVER_FROM_NOTIFICATION =
            "ACTION_RESTART_SERVER_FROM_NOTIFICATION"
    }

    private lateinit var server: KtorDeviceServer

    private lateinit var webServerCheckDataStore: WebServerCheckDataStore
    private var isServiceRunning = false
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var serverStateFlow: StateFlow<String>

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate() {
        super.onCreate()
        val appDependencies = (application as MyApplication).appDependencies
        val dataStore = appDependencies.dataStore
        webServerCheckDataStore = WebServerCheckDataStore(dataStore)
        registerNetworkCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                startForegroundService()
            }

            ACTION_STOP_FOREGROUND_SERVICE, ACTION_STOP_SERVICE_FROM_NOTIFICATION -> {
                stopForegroundService()
            }

            ACTION_RESTART_SERVER_FROM_NOTIFICATION -> {
                restartServer()
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
            webServerCheckDataStore.saveServiceRunningState(true)
        }

        createNotificationChannel()
        var notificationText = "Подготовка к запуску сервера..."
        val notification = createNotification(notificationText)
        startForeground(NOTIFICATION_ID, notification)

        initServer()
        //checkServerStatus()
    }

    private fun initServer() {
        serviceScope.launch {
            try {
                server = KtorDeviceServer(this@LocalServerService)
                startServer()

                server.serverState.collectLatest { state ->
                    if (state.isNotEmpty()) {
                        updateNotification(state)
                    }
                }
            } catch (e: Exception) {
                isServiceRunning = false
                Log.d("slax", "Ошибка запуска сервера: ${e.message}")
            }
        }
    }

    private fun checkServerStatus() {
        serviceScope.launch {
            server.serverState.collectLatest { state ->
                if (state.isNotEmpty()) {
                    updateNotification(state)
                }
            }
        }
    }

    private fun stopForegroundService() {
        //if (!isServiceRunning) return

        isServiceRunning = false
        serviceScope.launch {
            webServerCheckDataStore.saveServiceRunningState(false)
        }

        stopServer()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationStopped = createNotification(SERVICE_NOTIFICATION_CONTENT_STOPPED, true)
        notificationManager.notify(NOTIFICATION_ID, notificationStopped)

        stopForegroundCompat()
        stopSelf()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            SERVICE_CHANNEL_ID,
            "Локальный веб-сервер",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun createNotification(contentText: String, canDismiss: Boolean = false): Notification {
        val stopIntent = Intent(this, LocalServerService::class.java).apply {
            action = ACTION_STOP_SERVICE_FROM_NOTIFICATION
        }
        val stopPendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val restartServerIntent = Intent(this, LocalServerService::class.java).apply {
            action = ACTION_RESTART_SERVER_FROM_NOTIFICATION
        }
        val restartServerPendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                0,
                restartServerIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val builder = NotificationCompat.Builder(
            this,
            SERVICE_CHANNEL_ID
        )
            .setContentTitle(SERVICE_NOTIFICATION_TITLE)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(!canDismiss)
            .addAction(R.drawable.ic_stop, "Остановить", stopPendingIntent)
            .addAction(
                R.drawable.outline_confirmation_number_24,
                "Перезапустить",
                restartServerPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (canDismiss) {
            builder.setAutoCancel(true)
        }

        return builder.build()
    }

    private fun updateNotification(status: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(status)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isServiceRunning = false
        serviceScope.launch {
            webServerCheckDataStore.saveServiceRunningState(false)
        }
        serviceScope.cancel()
        unregisterNetworkCallback()
        super.onDestroy()
    }

    private fun registerNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                restartServer()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                stopServer()
            }
        }

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun startServer() {
        server.startServer()
    }

    private fun stopServer() {
        if (::server.isInitialized && server.isRunning())
            server.stopServer()
    }

    private fun restartServer() {
        stopServer()
        initServer()
    }
}