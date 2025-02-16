package com.synngate.twowaysync.util

import android.util.Log
import com.synngate.twowaysync.data.repository.LogRepository
import com.synngate.twowaysync.domain.model.LogDetails // <---- Импорт LogDetails
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object LogHelper {

    private const val TAG = "LogHelper" // <---- Добавим TAG для логов LogHelper

    private lateinit var logRepository: LogRepository

    fun init(logRepository: LogRepository) {
        this.logRepository = logRepository
    }

    suspend fun log(message: String) {
        if (!::logRepository.isInitialized) {
            android.util.Log.e(TAG, "LogHelper is not initialized! Call LogHelper.init(logRepository) first.")
            return
        }
        val logDetails = LogDetails(
            id = null,
            event = message,
            level = "INFO",
            dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
        )

        // Добавляем логирование ВОКРУГ вызова insertLog с использованием android.util.Log
        Log.d(TAG, "LogHelper.log() - Before calling insertLog: message = $message") // <---- ЛОГ ПЕРЕД ВЫЗОВОМ

        try {
            logRepository.insertLog(logDetails) // <----  РАСКОММЕНТИРОВАЛИ ВЫЗОВ insertLog
            Log.d(TAG, "LogHelper.log() - Call successfull insertLog: message = $message") // <---- ЛОГ ПОСЛЕ УСПЕШНОГО ВЫЗОВА
        } catch (e: Exception) {
            Log.e(TAG, "LogHelper.log() - Error on calling insertLog: message = $message", e) // <---- ЛОГ В СЛУЧАЕ ОШИБКИ
        }

        Log.d(TAG, "LogHelper.log() - After call (or attempt to call) insertLog: message = $message") // <---- ЛОГ ПОСЛЕ БЛОКА try-catch
    }
}