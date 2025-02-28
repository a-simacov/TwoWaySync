package com.synngate.twowaysync.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.synngate.twowaysync.di.AppDependencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PingActiveServerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val activeApiService = AppDependencies.activeApiService
                ?: return@withContext Result.failure(workDataOf("error" to "No active server"))
            val response = activeApiService.echo()
            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.failure(workDataOf("error" to "Server returned ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }
}