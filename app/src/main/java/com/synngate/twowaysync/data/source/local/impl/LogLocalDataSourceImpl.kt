package com.synngate.twowaysync.data.source.local.impl

import android.util.Log
import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.data.source.local.dao.LogDao
import com.synngate.twowaysync.data.source.local.LogLocalDataSource
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity
import com.synngate.twowaysync.domain.common.LogFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogLocalDataSourceImpl(
    private val logDao: LogDao
) : LogLocalDataSource {

    private val TAG = "LogLocalDataSourceImpl"

    override suspend fun getLogs(filter: LogFilter?): Result<List<LogDetails>> { // <---- Возвращаем List<LogDetails>
        return try {
            val logEntitiesFromDb = withContext(Dispatchers.IO) {
                logDao.getAll() // Получаем LogEntity из DAO
            }
            // Конвертируем LogEntity в LogDetails (если необходимо)
            val logDetailsList = logEntitiesFromDb.map { logEntity ->
                logEntityToLogDetails(logEntity) // <---- Функция конвертации (реализуем ниже)
            }
            Result.Success(logDetailsList) // Возвращаем Result.Success со списком LogDetails

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun insertLog(logDetails: LogDetails): Result<Unit> { // <---- Метод insertLog принимает LogDetails
        return try {
            val logEntity = logDetailsToLogEntity(logDetails) // <---- Конвертируем LogDetails в LogEntity
            withContext(Dispatchers.IO) {
                logDao.insert(logEntity) // <---- Используем LogDao для вставки LogEntity
            }
            Result.Success(Unit) // Возвращаем Result.Success<Unit>

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getLogsCount(): Result<Int> { // <---- Реализация getLogsCount()
        val count = withContext(Dispatchers.IO) {
            logDao.getLogsCount()
        } // Вызовите logDao.getLogsCount() для получения количества
        Log.d(TAG, "getLogsCount() - Count from DAO: $count") // <---- ДОБАВЛЕНО ЛОГИРОВАНИЕ ЗДЕСЬ
        return Result.Success(count)
        //return@withContext count // Верните количество
    }

    // Функции конвертации (пример реализации, адаптируйте под ваши классы)
    private fun logEntityToLogDetails(logEntity: LogDetailsEntity): LogDetails {
        return LogDetails(
            id = logEntity.id,
            event = logEntity.event,
            level = logEntity.level,
            dateTime = logEntity.dateTime
        )
    }

    private fun logDetailsToLogEntity(logDetails: LogDetails): LogDetailsEntity {
        return LogDetailsEntity(
            id = logDetails.id,
            event = logDetails.event,
            level = logDetails.level,
            dateTime = logDetails.dateTime
        )
    }
}