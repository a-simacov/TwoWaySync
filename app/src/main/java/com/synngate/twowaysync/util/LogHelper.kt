package com.synngate.twowaysync.util

import android.util.Log
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.domain.model.LogDetails // <---- Импорт LogDetails
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object LogHelper {

    private const val TAG = "LogHelper"

    private lateinit var logRepository: LogRepository

    fun init(logRepository: LogRepository) {
        this.logRepository = logRepository
    }

    suspend fun log(message: String) {
        if (!::logRepository.isInitialized) {
            Log.e(TAG, "LogHelper is not initialized! Call LogHelper.init(logRepository) first.")
            return
        }
        val logDetails = LogDetails(
            id = null,
            event = message,
            level = "INFO",
            dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
        )

        try {
            logRepository.insertLog(logDetails) // <----  РАСКОММЕНТИРОВАЛИ ВЫЗОВ insertLog
        } catch (e: Exception) {
            Log.e(TAG, "LogHelper.log() - Error on calling insertLog: message = $message", e) // <---- ЛОГ В СЛУЧАЕ ОШИБКИ
        }
    }
}